// 호이스팅(hositing -> 끌어올리기)
// var, let, const, function -> 모두 호이스팅이 일어나기는 한다
// a; // ReferenceError: a is not defined
// console.log(a); // 여기에는 a가 없음. // ReferenceError: a is not defined
a = "a"; // var a = "a"; // 알아서 해석되면서 붙은 것이기 때문에
console.log(a);

// var b; 호이스팅이 되어서 undefined 되어 있는 상황으로 선언부터 해서 위로 올라가 있는 것
console.log(b); // undefined
var b = "b";
console.log(b);

// initialization
// console.log(c); // ReferenceError: Cannot access 'c' before initialization
let c = "c";
console.log(c);

// console.log(d); // Cannot access 'd' before initialization
const d = "d";
console.log(d);

console.log(func); // undefined x. func.
console.log(func()); // 호이스팅 때문에 undefined가 나온게 아니라 return이 없어서임.
function func() {
  console.log("func()");
  // return 값이 없는 function은 그 결과를 undefined로 감지
} // 본문까지 모두 올라간거기 때문에

// 스코프 -> 호출 범위, 생명주기.
// 호출 범위 -> 변수 -> 어디까지 부를 수 있느냐. 블록, 함수가 종료되었을 경우 그게 유지 되는가?
// var : 함수 스코프.
// let, const : 블록 스코프

{
  var x = "java"; // 블록 밖에 영향을 미칠 수 있음 (function block이 아니라면)
  let y = "python"; // ?
  let z = "sql"; // ?
  console.log(x, y, z);
}
// console.log(x, y, z); // y is not defined

let tmp = 0;
for (let i = 0; i < 50; i++) {
  // for (var i = 0; i < 50; i++) {
  i *= 5;
  i += i;
  console.log(i); // for문에서 나오는 i는 스코프의 생애주기 상 이 블록을 넘어갈 수 없음 (let으로 한다면.) -> var로 하면 가능하기는 한데 권장하지 않음
  if (i < 50) {
    tmp = i;
  }
}
// console.log(i);
console.log(tmp);

// 예외처리 -> try. catch. try {} -> 밖으로 못나오는 이슈...

let v1 = 10;
let v2 = 11;
let v3 = 12;
function f1(p1, v1) {
  // 매개변수를 통해 전달을 받는다면...
  //   let p1 = 30;
  //   let v1 = 30; // 같이 사용할 수는 X 매개변수와 내부 변수는 같이 사용할 수는 없음.
  let v2 = 31; // 주석 처리가 되어있어서 이 변수를 사용하지못한다면
  // 같은 js 파일내에 가장 가까운 v2를 찾는다 -> 같은 블록, 같은 함수, 같은 파일
  let v3 = 32;
  console.log(v1, v2, v3, p1);
  {
    let v1 = 40; // 새롭게 블록스코프를 열면 기존의 매개변수를 무시하고 새롭게 사용할 수 있음.
    let v2 = 41;
    let v3 = 42;
    let p1 = 43;
    console.log(v1, v2, v3, p1);
  }
  console.log(v1, v2, v3, p1);
}
f1(21, 22);