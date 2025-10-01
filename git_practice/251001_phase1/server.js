// es-module -> import, commonjs -> require
const express = require("express"); // express 안에 있는 이미 구현되어 있는 코드들을 express 객체 형태로 불러오겠다
const cors = require("cors"); // 설치한 의존성 패키지 cors 불러오기
const app = express(); // () -> 호출해서 사용하겠다

// 포트 -> 컴퓨터 서비스가 1개만 있는게 아님. email, db, server 1, server 2...
// 1 ~ 2xxxx. => 이 번호로 오세요...
const port = 3000; // cra. next -> express. / 5173.
// localhost -> 3000. / 5500? <-> 구분해주는 의미.

// cors 미들웨어 등록
app.use(cors()); 

// get, post...
// app.방식(접속경로, 핸들러)
// localhost:3000/
app.get("/", (req, res) => {
  // req -> request -> 전달 받은 데이터나 요청사항
  // res -> response -> 응답할 내용/방식을 담은 객체
  //   res.send("hello");
  res.send("bye");
});

// DOM listener / server '대기' -> 특정한 요청. -> 응답.
app.listen(port, () => {
  console.log(`서버가 ${port}번 포트로 실행 중입니다.`);
});
