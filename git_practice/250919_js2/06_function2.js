// 함수 표현식
// 변수에 함수를 넣는다
// func(); // ReferenceError: Cannot access 'func' before initialization
const func = function () {
  console.log("func!!!");
};
func();

// 화살표 함수
const arrowFunction = () => {};
// function 키워드 생략
// (패러미터) => {본문}
// 자바의 경우 : (패러미터) -> {본문}
// 파이썬의 경우 : lambda : ...

// 매개변수가 1개인 경우
// const parameter1 = a => {};
// 값을 바로 return하는 경우
const f = () => 1 + 1; // 중괄호를 생략
const f2 = () => {
  return 1 + 1;
}; // f, f2는 같은 구문. -> Return 값을 바깥으로 출력 -> 별로도 복잡한 다른 구문 없이 연결되는 형태로 return만 존재한다면 생략하고 바로 해당 구문만 {}와 return 생략하고 작성.

// add
const result1 = add1(10, 13);
function add1(a, b) {
  // 호이스팅 -> 본문까지 위로 올라가기 때문에...
  return a + b;
} // 함수 선언식
console.log("result1", result1);
const add2 = function (a, b) {
  return a + b;
}; // 함수 표현식
const result2 = add2(10, 13);
console.log("result2", result2);
// 화살표 함수
const add3 = (a, b) => {
  return a + b;
};
const result3 = add3(10, 13);
console.log("result3", result3);
const add4 = (a, b) => a + b; // 암시적 표현
const result4 = add4(10, 13);
console.log("result4", result4);