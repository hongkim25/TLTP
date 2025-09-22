// a.b; // ReferenceError: a is not defined
// 실행 정지인데...
try {
  // 실행 코드
  a.b;
} catch {
  // 오류가 났었을 때의 처리
  console.log("에러가 났습니다");
}

try {
  // 실행 코드
  a.b;
} catch (e) {
  // 매개변수로 받아줘야한다
  //   console.error(e); // 실행이 정지되진 않고 error stack -> 여기까지 실행하면서 무슨 에러가 났었는지를 알려주는 과정
  console.log(e.name); // 에러의 이름
  // name을 활용해서 if문으로 분기를 함. -> 네트워크 에러 처리할 때...
  console.log(e.message); // 에러의 메시지
}

try {
  if (Math.random() > 0.5) {
    a.b;
  }
  console.log("버그 안남");
} catch {
  console.log("버그 남");
} finally {
  console.log("어쨌든 실행됨");
}

function f() {
  // finally 없을 시엔 catch가 없어도 된다
  try {
    return "a";
  } finally {
    return "b"; // finally 정말정말 마지막에 실행되기 때문에 return으로
    // 심지어 함수가 종료되어도 그 다음에 다시 return을 덮어씌움
  }
}
console.log(f());

let a = 0; // 바깥에 빈 값이라도 초기화를 해놓고.
try {
  // try도 블럭이다 try 내부에 들어간 변수
  // try은 외부 네트워크 호출할 때 많이 씀 <- 데이터를 받아온다는 의미
  // 이게 전달이 되어야하는데 return 되는 거면 상관 없음
  // try문을 넘어서 '가공'되어야한다면...
  //   let a = 0;
  a += 100;
} catch {}
console.log(a); // ReferenceError: a is not defined

// 커스텀 에러 -> 던진다 -> throw

try {
  // 강제 탈출 비슷하게 된다
  for (let i = 0; i < 50; i++) {
    if (Math.random() > 0.5) {
      throw new Error("새로운 에러! " + i); // return과 비슷한 방식...
    }
  }
} catch (e) {
  console.log(e.name);
  console.log(e.message);
  throw e; // 다시 던질 수도 있음. rethrow
}