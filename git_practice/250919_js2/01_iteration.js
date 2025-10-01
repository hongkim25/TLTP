// while -> 특정 조건(boolean) 혹은 굳이 종료 조건을 일단은 명시하지 않고 반복하다가 중간에 if라든가 등으로 break.

// 반복할 때는 '횟수'를 정해놓고 하게 됨
// for.

// for (초기식; 조건식(종료식); 증감식) {}
// 초기식 -> 변수 -> let i = 0;
// 특정한 반복에 있어서 현재까지의 반복을 추적해주는 변수를 만드는 행위
// 조건식(종료식) -> 이게 false가 되면 종료. (이게 true인 상황에서 계속 반복)
// 10회 반복하겠다 -> i - 0 0, 1, 2, 3, 4...., 9. [10]
// 증감식 -> 꼭 증감연산자일 필요는 없으나 (한 줄로 나타낸 구문.) 한 번의 반복이 돌고 나서 (일반적으로는)
// 초기식으로 만들어준 변수에 변화를 일으킬 식
for (let i = 0; i < 10; i++) {
  // 단순 10번 실행
  console.log("i", i);
}

// 1부터 10까지 더하는 경우
let sum = 0;
for (let i = 1; i <= 10; i++) {
  sum += i;
}
console.log("sum", sum);
// 3의 배수를 출력
for (let i = 3; i < 50; i += 3) {
  console.log(i);
  // 이 내부에서 다시금 i 선언하려하면 에러가 생기는데...
}
// i를 쓰려고 하면 문제가 X.

// 어? 뭔가 이상하다 i가 왜 재활용할 수 있음?
// let은 block scope. for () {}. 내부에는 같은 이름을 쓰는데, 바깥에서는 그걸 모르는 것.

// 중첩 for문.
for (let i = 0; i < 5; i++) {
  for (let i = 0; i < 5; i++) {
    // 같은 이름을 써버리면은 X
    console.log(i, i);
  }
}
for (let i = 0; i < 5; i++) {
  for (let j = 0; j < 5; j++) {
    console.log(i, j);
  }
}

// 흐름제어 -> continue, break. / 무한반복
// 1. continue, break.
for (let i = 0; i < 10; i++) {
  // if (i % 2 != 0) { // 나머지 연산 시 -> 나머지가 있으면 0이 아님 -> 2로 나눴을 때 나머지 있으면 홀수
  if (i % 2) {
    // 홀수를 검증하는 방식
    // 홀수인 경우에는 생략
    continue;
  }
  console.log("i", i);
}
let result = 1;
for (let i = 1; i <= 50; i++) {
  //   if (result >= 1000) {
  //     break;
  //   }
  if (result >= 1000) break; // {}가 1줄이면 생략.
  result *= i;
}
console.log(result);

// for문도 무한 반복이 될까?
for (;;) {
  // 종료식을 생략하면 ?
  break;
}