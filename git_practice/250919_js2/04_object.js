// JavaScript Object.
// Key. Property -> 호출할 수 있는 내부 데이터.
// -> 함수. 데이터 묶음.
// 묶음? Array. -> Array. -> Object. 특수한 객체 -> Array.

const obj = {
  텍스트: 1, // 자동으로 포맷팅 당해버림 "a": 1
  // -ㄹㄹ: 4 // 이런건 안된다
  a: "이런건된다",
  "a?_1": 1, // 키 형태로의 문법으로만 입력 가능
  hello: function () {
    console.log("hello!");
  },
  greet() {
    console.log("hello!");
  },
  // 함수나 메서드를 직접 넣을 수 있다
  // 딕셔너리로 이해해야할지 클래스로 이해해야할지 구조체로 이해해야할지... 짬뽕이다...
}; // { "키" : 값 } { 키 : 값 }
// "키": 값, {프로퍼티 : 값}
// {"텍스트" '': 값}, {변수: 값}
// $_ 특수문자사용 제약, 스페이스. 카멜케이스

// 보장된 순서가 없는 경우
// array 처럼 0, 1, 2, 3... 숫자 순서가 있는 건 아님.
// 숫자 키를 못 넣는 건 아님
const arr = {
  0: "안녕",
  1: "ㅎㅎ",
  2: "ㅎㅎ",
};
// 넣은 키는 객체.프로퍼티 혹은 객체["키"]
const obj2 = {
  a: "123",
  "a-1": "456",
  0: "567",
};
console.log(obj2.a);
console.log(obj2[0]);
console.log(obj2["a"]);
console.log(obj2["a-1"]);
obj2[0] = 1000;
obj2["a"] = 10000;
obj2["a-1"] = 100000;
console.log(obj2[0]);
console.log(obj2["a"]);
console.log(obj2["a-1"]);
console.log(obj2);
delete obj2[0];
console.log(obj2);
console.log(obj2[0]);

const array = [1, 2, 3, 4, 5];
console.log(array);
array["a"] = "aaa";
console.log(array);
array[0.5] = "aaa";
console.log(array);
array[-1] = "aaa";
console.log(array);

// java할 때 주소값이랑 깊은복사 얕은복사 총 정리합시다...

const user = {
  name: "kim",
  age: 20,
  salary: 100,
  email: "kim@java.com",
};

for (const key in user) {
  console.log(key);
  console.log(user[key]);
}

console.log("name" in user);
console.log("lunch" in user);

Object; // class.
Object.values;
Object.keys;

console.log(Object.keys(user));
console.log(Object.values(user));
console.log(Object.entries(user));