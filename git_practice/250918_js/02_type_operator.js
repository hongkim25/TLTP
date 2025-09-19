// 변수, 타입, 연산자

// var a; // undefined -> 선언 및 undefiend 할당
console.log(a); // 없는 변수를 쓸 수 있는 이슈가 생기는 것.

var a; // 실질적으로 등장을 하는 시점이 여기인데...

var b = 100; // 변화하기를 원하지 않아.

// ....

var b = 200; // 한 번 더 할당을 했어.

// let, const.

// 뒤에 나오는 걸 미리 쓰면 에러남 (let, const)
// console.log(c); // ReferenceError: Cannot access 'c' before initialization

let c;
c = 10;
c = 100; // 재할당은 막을 수 없음.
c = c + 10;
c = c + 10;
console.log(c); // 120 -> 어떠한 연산의 결과. 변화하는 값을 처리할 때 let.
let d = 100; // 초기화 var와 같은 용법
// let c; // SyntaxError: Identifier 'c' has already been declared

// console.log(e); // ReferenceError: Cannot access 'e' before initialization
const e = 1000;
// const e; // 한 번에 할당까지하는 초기화를 진행해줘야한다
// SyntaxError: Missing initializer in const declaration
// e = 10000; // TypeError: Assignment to constant variable.
// 변동할 여지가 없으면 const 권장하긴 함. 객체

// 해당 변수명을 다른 변수에 대입 연산자 (=)으로해서 넣어서 쓰거나, 구문(statement)나 표현식(expression)에서 사용할 수 있다
a + 1;
let abc = a + b + c;

// 타입 - 자료형(data type) ->
1;
"글자"[
  // 리터럴
  "배열"
];
{
  ("객체");
}
// 이런식으로 되어 있는 복잡한 형태의 데이터들이 어떻게 구조화되어있고 저장되어있고 연관된 기능(함수, 메서드)이 무엇인지 등을 알려주고 사용하는 방식 -> 타입
// 언어들마다 다름
// JavaScript - 7가지. 참조타입.
// 기본/원시타입 - 실행환경 - 기본적으로 지원 (공통적으로 언어 자체에 있는 것)
// 참조타입 - API/인터페이스 제공, 개발자 라이브러리/패키지 만들어내는 타입 (실행환경, 개발환경)
// 5가지(자세히 봄). 2가지.(적당히.)
console.log("-- Number --");
// Number (숫자) Short, Long, Integer..., Byte... X
// 정수, 실수.
console.log(1); // 숫자를 별도의 특수문자 없이 그대로 입력할 수 있음
console.log(1.01); // 소수를 넣으면 소수점이 붙어서 실수형태
console.log(4 / 7);
// 계산이 가능하다 (산술연산)
// 문자열
// 문자 -> 자바스크립트에는 별도로 '문자'가 없습니다. 다 문자열 'String'
// 한글자 한글자면 '문자'인데 문자들의 배열. 배열 -> 순서가 있게 공통의 형태로 나열된 자료 구조
// '', "", `` 묶어서 나타냄. `` -> `앞뒤에${변수명}텍스트`
console.log("-- String --");
console.log("안녕하세요");
const s = "안녕히가세요";
console.log(s);

// boolean : George Boole -> 참/거짓.
// T/F Bool, Bloolean.
console.log(true);
console.log(false);
// 값을 연산자나 함수 등으로 '평가'해서 boolean -> 1(참가)/0(거짓)
// truthy, falsy, 연산자.

// Null과 undefined
const n = null;
const ud = undefined;
let ud2;
console.log(n, ud, ud2);
const aa = {};
console.log(aa.bb);
console.log(aa["cc"]);
// console.log(ud.bb); // TypeError: Cannot read properties of undefined (reading 'bb')

console.log(typeof b); // 담은 변수
console.log(typeof "aaa");
console.log(typeof undefined);
console.log(typeof null);

// symbol, bigint
const string1 = "hello";
const string2 = "hello";
console.log(string1 == string2);
const symbol1 = Symbol("hello");
const symbol2 = Symbol("hello");
console.log(symbol1 == symbol2);
// bigint -> 엄청 큰 숫자 long? 몇십억보다 더 큰 숫자. -> 문자열로 바꿔버리면 된다

// 7가지의 원시타입, 기본타입
// Number, String, Boolean, Null, Undefined, Symbol, Bigint
// 참조타입
// {}, [], class, ... -> 값 자체가 메모리에 들어가는게 아니라 해당 데이터를 찾을 수 있는 '주소'가 변수에 들어가는 꼴 -> 변수 자체를 재할당하지 않아도 내부에서 수정이 가능한 데이터 타입.
// [1, 2, 3, 4, 5] -> [] 배열을 찾을 수 있는 정보 -> [] 배열 자체는 어느 정도 수정이 가능.