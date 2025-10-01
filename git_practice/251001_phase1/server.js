const express = require("express");
const cors = require("cors");
const dotenv = require("dotenv");
const path = require("path"); // Add at top with other requires
const { createClient } = require("@supabase/supabase-js"); // 구조분해 할당

dotenv.config(); // .env -> KEY => SUPABASE_KEY
// NODE -> process.env (환경변수) // cf. env file

// const supabaseKey = process.env.SUPABASE_KEY;
// const supabaseUrl = process.env.SUPABASE_URL;
const { SUPABASE_KEY: supabaseKey, SUPABASE_URL: supabaseUrl } = process.env;
console.log("supabaseKey", supabaseKey); // 확인 (npm run dev)
console.log("supabaseUrl", supabaseUrl);
const supabase = createClient(supabaseUrl, supabaseKey);

const app = express();
const port = 3000;

app.use(cors());
app.use(express.json()); // req.body -> json.


app.use(express.static(__dirname)); // Add before routes

app.get("/", (req, res) => {
  res.sendFile(path.join(__dirname, "index.html"));
});

app.get("/plans", async (req, res) => {
  const { data, error } = await supabase.from("tour_plan").select("*");
  if (error) {
    return res.status(400).json({ error: error.message });
  }
  res.json(data);
});

app.post("/plans", async (req, res) => {
  const plan = req.body;
  const { error } = await supabase.from("tour_plan").insert(plan);
  if (error) {
    return res.status(400).json({ error: error.message });
  }
  res.status(201).json();
});

app.delete("/plans", async (req, res) => {
  const { planId } = req.body;
  const { error } = await supabase
    .from("tour_plan") // table
    .delete() // 삭제
    .eq("id", planId); // eq = equal = id가 planId
  if (error) {
    return res.status(400).json({ error: error.message });
  }
  res.status(204).json(); // noContent
});

app.listen(port, () => {
  console.log(`서버가 ${port}번 포트로 실행 중입니다.`);
});