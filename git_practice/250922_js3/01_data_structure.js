// 객체
const obj = {
  // 키: 값
  key1: 1234,
  1: 1234, // 값은 중복되도 괜찮음
  1: 1235, // 키가 중복될 경우에는?
  // 숫자의 경우에는 문자열로 변환되어서 키로 들어가는 것
  // 가장 최신으로 작성한 내용이 덮어씌우기를 함
  // ...rest -> 이걸로 default한 다음에
  // 추가로수정하고자하는키 : 값 해서 테크닉이 있음. (덮어씌우기)
  // 다른변수를 키값으로 삼는다던가 x. 함수 등을 키로 쓸 순 없음.
};

console.log(obj);
console.log(obj.key1); // 변수명명 규칙을 준수했을 경우 (알파벳과 $_로 시작하고, 중간에 공백없을 것)
console.log(obj[1]); // 숫자로 시작하거나 공백을 포함하는 경우
console.log(Object.keys(obj)); // 키들의 배열
console.log(Object.values(obj)); // 값들의 배열
console.log(Object.entries(obj)); // [키, 값]의 배열
// for in.

// Array
// 0개 이상의 데이터 묶음 -> 0개 이상 -> [] 빈 배열.
const arr = [1, 2, 3];
// CRUD...
// for of.
console.log(arr);
arr.length = 0; // 길이 -> arr.length -> 길이.
console.log(arr); // length를 0으로 하면 지워진다

// Map
// Object?
const dataObj = {
  name: "kim",
  job: "programmer",
  age: 100,
}; // 레코드.
// for (const v of dataObj) {
// TypeError: dataObj is not iterable
for (const v of Object.entries(dataObj)) {
  console.log(v);
}
// Map : 고정된 키 호출 -> 고정적인 값을 받는 저장형태.
// - 다양한 타입의 키를 지원 / 연산이 최적화 -> 사용 방법이 조금 더 번거로움?
// - 관련된 존재 여부 관련된 연산들...
// -> 자바스크립트는 원래 별도로 지원을 안해주다가 (Object가 충분히 비슷하니까...)
const map = new Map(); // 비어있는 용기를 하나 만들고.
// map.set(키이름, 값) : 데이터를 추가
map.set("name", "lee"); // key - value를 세팅
map.set("job", "engineer");
map.set("age", 1000);
console.log(map);
// map.get(키이름) : 해당 데이터를 조회
console.log(map.get("age"));
console.log(map.get("carrer")); // undefined (없는 데이터 조회 시)
// map.has(키이름) : 특정한 키 존재 여부 확인
console.log(map.has("name")); // boolean -> true/false
console.log(map.size); // 크기
for (const v of map) {
  console.log(v); // entries
}
for (const [key, value] of map) {
  console.log(key, value); // 구조 분해 할당
}
// keys, entires, values ... -> Objects와 유사
map.clear();
console.log(map); // 내용을 지워줌
const map2 = new Map([["a", "a"]]); // [[키이름, 값], ...]의 쌍들의 배열을 new Map([배열])을 넣어서 한 번...
console.log(map2);
// 같은 구조로 사용했을 경우에 Map 속도나 기능이 더 다양함 (더 최신으로 나온 문법)

// Set
const set = new Set();
// set -> 키 = 값.
// key -> map, set. -> 호출할 key -> hashing -> a2432fkda -> [...?] -> 중복을 허용하지 않음
// [중복을 허용하지 않음]
// 1. 같은 값을 들어오려고 하면 에러를 도출한다 (unique하다) <- 검사하는 과정이 필요. 소프트웨어의 영역.
// 2. (없으면 추가하고) 같은 값이 들어오면 덮어씌워버린다 (upsert한다) -> map, set.
const data = [
  "김자바",
  "김자바",
  "김자바",
  "이디비",
  "이디비",
  "박코딩",
  "박코딩",
  "박코딩",
  "박코딩",
  "박코딩",
];
console.log(data);
const result = [];
for (const name of data) {
  //   console.log(v);
  if (!result.includes(name)) {
    // 특정한 배열에 특정한 요소가 포함되어 있는지는 includes로 검사
    // ! -> 없으면
    console.log(name);
    result.push(name); // 2번째부터는 이미 있는 이름이니까 안잡히겠죠?
  }
}
console.log(result);

const setResult = new Set(data);
console.log(setResult); // 중복을 추려줌
const setArray = [...setResult];
console.log(setArray);

const set1 = new Set();
set1.add(1);
set1.add(2);
set1.add(1); // 똑같은 데이터를 넣었을 때 무시된다 (이미 존재하는 데이터를 넣었을 때 무시된다)
// 추가할 때 set이 아니라 add를 쓴다는 것 제외하곤 map가 거의 똑같은데
// get은 없다. 왜 없을까?
console.log(set1);