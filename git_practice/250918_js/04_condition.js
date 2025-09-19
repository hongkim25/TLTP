// 흐름제어
// 특정한 조건 -> 분기. 반복.

// 삼항 연산자. -> 값만. 분기. -> console.log(...)

// 반반의 확률로

console.log(Math.random() > 0.5 ? "당첨" : "당첨 아님"); // 값만.

// 조건에 따른 분기
// if (조건 boolean) { 블록 } // let, const.
if (Math.random() > 0.5) {
  console.log("우와 당첨이네요 진짜 부럽다");
  console.log("이 돈을 드리겠습니다");
  console.log("정말 정말 부럽네요");
}
// if (조건) { 블록 } else { 블록 }
if (Math.random() > 0.5) {
  console.log("우와 당첨이네요 진짜 부럽다");
  console.log("이 돈을 드리겠습니다");
  console.log("정말 정말 부럽네요");
} else {
  console.log("우와 탈락이시네요 진짜 안됐다");
  console.log("이 돈을 안 드리겠습니다");
  console.log("정말 정말 안됐네요");
}

// Math.random() // 0 이상 1 미만의 숫자.
// Math.random() * 6 // 0 이상 6 미만의 숫자.
// Math.random() * 6 + 1 // 1 이상 7 미만의 숫자.
const dice = Math.floor(Math.random() * 6 + 1);
console.log("주사위의 눈", dice);
if (dice >= 5) {
  console.log("승리!");
} else if (dice >= 3) {
  // 앞의 if가 처리 되고 나서 그 나머지의 상황에서만 작동하는 로직
  console.log("무승부!");
} else {
  console.log("패배!");
}

const dice2 = Math.floor(Math.random() * 6 + 1);
console.log("dice2", dice2);
// switch (변수명) { case 일치하는지 확인할 값... : 실행할 로직, defaut : 실행할 로직}
switch (dice2) {
  case 1:
    // 한 번 통과가 되면 뒤에까지 다 break를 만날 때까지 모두 통과 -> fall through
    console.log("1번이 뽑혔습니다.");
    break;
  case 2:
    console.log("2번이 뽑혔습니다.");
    break;
  case 3:
    console.log("3번이 뽑혔습니다.");
    break;
  default:
    console.log("4-6 사이의 값입니다.");
}

// while -> if (조건) {} 1번. while (조건) {} true 계속 실행.
let flag = true;
while (flag) {
  console.log("실행 중");
  if (Math.random() > 0.5) {
    flag = false;
  }
}
// let w = 0;
// while (w < 10) {
//   w++;
//   console.log(w + "번째 반복");
// }

// while (1) {
// while (true) {
//   // true
//   // 중간에 중지를 시켜주고 싶다...
//   console.log("이번에는 멈출까요?");
//   if (Math.random() > 0.5) {
//     console.log("멈췄다!");
//     break; // 지금까지 반복을 멈춰주는 키워드
//     // 이 이상으로 진행하지 않음.
//   }
//   // 여기를 도달하지 않음
//   console.log("안 끝났다!");
// }

while (true) {
  console.log("이번에는 멈출까요?");
  if (Math.random() > 0.5) {
    continue; // 이 이상으로 진행하지 않고 다음 반복으로 넘어가게 해주는 키워드
  }
  // 여기를 도달하지 않음
  console.log("멈췄다!");
  break;
  console.log("여기는 절대로 도달하지 않는 포인트")
}

// 반복문으로 반복하는 3가지 방법
// 1. 조건을 제대로 넣고 해당 조건을 만족시키도록 하는 방법
// 2. (조건을 넣지 않고 무한히 반복하게 하며) 특정한 상황에서 종료하게 함 (break)
// 3. (...) 그냥 두면 종료(break)가 되는데 특정 상황에서 강제로 반복(continue)하게 함