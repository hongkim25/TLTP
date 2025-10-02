const express = require("express");
const cors = require("cors");
const dotenv = require("dotenv");
const { createClient } = require("@supabase/supabase-js");
const { GoogleGenAI } = require("@google/genai");
const { Groq } = require("groq-sdk");
// npm install multer
const multer = require("multer"); // 미들웨어 -> 변환, 체크.

dotenv.config();
const { SUPABASE_KEY: supabaseKey, SUPABASE_URL: supabaseUrl } = process.env;
console.log("supabaseKey", supabaseKey);
console.log("supabaseUrl", supabaseUrl);
const supabase = createClient(supabaseUrl, supabaseKey);
// 파일 처리
const storage = multer.memoryStorage(); // 메모리 -> 실행할 때 임시로 파일 관리
const upload = multer({ storage }); // 업로드를 처리해주는 미들웨어

const app = express();
const port = 3000;

app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
  res.send("bye");
});

app.get("/plans", async (req, res) => {
  const { data, error } = await supabase.from("tour_plan").select("*");
  if (error) {
    return res.status(400).json({ error: error.message });
  }
  res.json(data);
});

// upload.single("image") -> form 데이터 중에 image라는 속성(네임)을 req.file -> req.body.
app.post("/plans", upload.single("image"), async (req, res) => {
  const plan = req.body; // 여기서부턴 이미지 존재 여부로 분기
  console.log(req.file);
  if (req.file) {
    console.log("이미지 파일이 존재");
    // Date.now() -> 파일 중복을 막기 위해 시간을 나타내는 숫자를 앞에 접두사(prefix)로 작성
    const filename = `${Date.now()}_${req.file.originalname}`; // 확장자 .png 등을 알아서 붙여줌
    const { error: uploadError } = await supabase.storage
      .from("tour-images") // 버킷명.
      // buffer : 텍스트 형태로 나타낸 파일. mimetype : 파일의 속성. 형태.
      .upload(filename, req.file.buffer, {
        contentType: req.file.mimetype,
      });
    if (uploadError) {
      console.error("이미지 업로드 실패", uploadError);
      return res.status(400).json({ error: uploadError.message });
    }
    // 여기까지 진행하면 server 파일 업로드 된 셈
    const { data: urlData } = supabase.storage
      .from("tour-images") // 버킷명
      .getPublicUrl(filename); // 생성파일 이름 -> 공개 URL
    plan.image_url = urlData.publicUrl; // 내가 업로드한 파일의 접속 링크 -> DB

    // 이미지 분석
    const analysis = await analyzeImage(req.file.buffer, req.file.mimetype);
    console.log(analysis);
    const { gemini, groq } = analysis;
    plan.purpose += `\n뒤는 목적과 관련된 사진에 대한 설명입니다. ${gemini}`;
    plan.purpose += `\n뒤는 목적과 관련된 사진에 대한 설명입니다. ${groq}`;
  }
  const result = await chaining(plan);
  plan.ai_suggestion = result;
  const { minBudget, maxBudget } = await ensemble(result);
  plan.ai_min_budget = minBudget;
  plan.ai_max_budget = maxBudget;

  const { error } = await supabase.from("tour_plan").insert(plan);
  if (error) {
    return res.status(400).json({ error: error.message });
  }
  res.status(201).json();
});

app.delete("/plans", async (req, res) => {
  const { planId } = req.body;
  const { error } = await supabase.from("tour_plan").delete().eq("id", planId);
  if (error) {
    return res.status(400).json({ error: error.message });
  }
  res.status(204).json();
});

app.listen(port, () => {
  console.log(`서버가 ${port}번 포트로 실행 중입니다.`);
});

async function chaining(plan) {
  const ai = new GoogleGenAI({});
  const response = await ai.models.generateContent({
    model: "gemini-2.5-flash",
    contents: `
    [장소] ${plan.destination}
    [목적] ${plan.purpose}
    [인원수] ${plan.people_count}
    [시작일] ${plan.start_date}
    [종료일] ${plan.end_date}`,
    config: {
      responseMimeType: "application/json",
      responseSchema: {
        type: "object",
        properties: {
          prompt: {
            type: "string",
          },
        },
        required: ["prompt"],
      },
      systemInstruction: [
        {
          text: `제공받은 정보를 바탕으로 최적의 여행 계획을 세우기 위한 프롬프트를 작성해줘. 응답은 JSON 형식으로 {"prompt": "프롬프트 내용"} 형식으로 작성해줘.`,
        },
      ],
    },
  });
  const { prompt } = JSON.parse(response.text);
  console.log("prompt", prompt);
  const response2 = await ai.models.generateContent({
    model: "gemini-2.5-flash-lite",
    contents: prompt,
    config: {
      systemInstruction: [
        {
          text: "프롬프트에 따라 작성하되, 300자 이내 plain text(no markdown or rich text)로.",
        },
      ],
    },
  });
  return response2.text;
}

async function ensemble(result) {
  const groq = new Groq();
  const models = [
    "moonshotai/kimi-k2-instruct-0905",
    "openai/gpt-oss-120b",
    "meta-llama/llama-4-maverick-17b-128e-instruct",
  ];
  const responses = await Promise.all(
    models.map(async (model) => {
      // https://console.groq.com/docs/structured-outputs
      const response = await groq.chat.completions.create({
        response_format: {
          type: "json_object",
        },
        messages: [
          {
            role: "system",
            content: `여행 경비 산출 전문가로, 주어진 여행 계획을 바탕으로 '원화 기준'의 숫자로만 작성된 예산을 작성하기. 응답은 JSON 형식으로 {"min_budget":"최소 예산", "max_budget": "최대 예산"}`,
          },
          {
            role: "user",
            content: result,
          },
        ],
        model,
      });
      console.log(response.choices[0].message.content);
      const { min_budget, max_budget } = JSON.parse(
        response.choices[0].message.content
      );
      return {
        min_budget: Number(min_budget),
        max_budget: Number(max_budget),
      };
    })
  );
  console.log(responses);
  // NaN 문제 해결
  return {
    minBudget: Math.min(
      ...responses.map((v) => v.min_budget).filter((v) => v !== NaN)
    ),
    maxBudget: Math.max(
      ...responses.map((v) => v.max_budget).filter((v) => v !== NaN)
    ),
  };
}

async function analyzeImage(buffer, mimeType) {
  // Gemini 호출
  const ai = new GoogleGenAI({});
  const visionPrompt =
    "제공 받은 여행 관련 이미지를 분석하여, 어떠한 장소인지 어떠한 목적을 기대할 수 있는지를 한국어로 200자 이내로 적어주세요.";
  const b64 = buffer.toString("base64");
  const geminiResponse = await ai.models.generateContent({
    // https://ai.google.dev/gemini-api/docs/models?hl=ko
    model: "gemini-2.5-flash", // "gemini-2.5-flash-lite",
    contents: [
      {
        parts: [
          { text: visionPrompt },
          {
            inlineData: {
              data: b64,
              mimeType,
            },
          },
        ],
      },
    ],
  });
  // Groq 호출
  const groq = new Groq();
  const groqResponse = await groq.chat.completions.create({
    // https://console.groq.com/docs/vision
    model: "meta-llama/llama-4-scout-17b-16e-instruct",
    // meta-llama/llama-4-maverick-17b-128e-instruct
    messages: [
      {
        role: "user",
        content: [
          {
            type: "text",
            text: visionPrompt,
          },
          {
            type: "image_url",
            image_url: {
              url: `data:${mimeType};base64,${b64}`,
            },
          },
        ],
      },
    ],
  });

  return {
    gemini: geminiResponse.text,
    groq: groqResponse.choices[0].message.content,
  };
}