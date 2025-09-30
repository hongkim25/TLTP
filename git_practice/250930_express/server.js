const express = require("express"); // npm install express
const path = require("path");
const dotenv = require("dotenv"); // 불러들이기

dotenv.config(); // .env 안에 있는 GEMINI_API_KEY
// process.env.GEMINI_API_KEY

// tailwind, bootstrap -> 의존성. 설치.
const app = express(); // 객체
const port = 3000;

// Get -> Fetch, Get/Post
app.get("/", (req, res) => {
  // localhost:3000/ -> GET/접속 (브라우저를 통한 접속)
  //   res.send("Hello World!");
  //   res.send("Bye Earth!");
  res.sendFile(path.join(__dirname, "index.html"));
});

// ai -> fetch.
app.post("/gemini", async (req, res) => {
  // localhost:3000/gemini POST -> 응답
  // 1. fetch.
  const model = "gemini-2.5-flash-lite";
  const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent`;
  //   const apiKey = "";
  const apiKey = process.env.GEMINI_API_KEY;
  const headers = {
    "x-goog-api-key": apiKey,
    "Content-Type": "application/json",
  };
  const payload = {
    //
    contents: [{ parts: [{ text: "오늘 저녁 메뉴 추천" }] }],
  };
  const response = await fetch(url, {
    headers,
    method: "POST",
    body: JSON.stringify(payload),
  });
  // 2. 라이브러리. (sdk)
  //   return res.json({ result: "ok" });
  // 비슷해보인다? -> .candidates... -> 원하는 것만 선택해서 내보내거나...
  return res.json(await response.json());
});

// listen -> server를 구동. -> localhost:3000
app.listen(port, () => {
  console.log(`Example app listening on port ${port}`);
});