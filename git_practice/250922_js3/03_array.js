// forEach -> return 값이 없음 -> 그 자체로 실행되면서 소진된다
// for.
const arr1 = [1, 2, 3];
arr1.forEach((v, i) => {
  console.log(i, v);
});
arr1.forEach(console.log); // console.log -> () => function
// 전달받은 패러미터(매개변수)를 다 쓰겠다
// 자바에서는 전달받은 패러미터와 구현된 패러미터의 갯수가 맞아야 쓰겠다
// 자바스크립트는 쓸 수 있으면 쓰겠다

// map -> 변환된 '배열'
// 길이가 보존되는 사본.

// filter -> 통과된 '배열'
// 통과. -> 조건을 만족하는 경우만 남긴다 // boolean - true
const arr2 = [1, 2, 3, 4, 5, 6, 7, 8];
console.log(arr2.filter((v) => v % 2 == 0)); // 정석
console.log(arr2.filter((v) => !(v % 2))); // syntatic sugar
const arr3 = [
  {
    language: "java",
    score: 100,
  },
  {
    language: "java",
    score: 50,
  },
  {
    language: "java",
    score: 70,
  },
  {
    language: "python",
  },
  {
    language: "c#",
    score: 60,
  },
];
console.log(arr3.filter((v) => v.language == "java" && v.score > 60));

// java이거나 60점 이상
console.log(arr3.filter((v) => v.language == "java" || v.score >= 60));
// java이고, (필터링하고) / 60점 이상 (필터링)
console.log(
  arr3.filter((v) => v.language == "java").filter((v) => v.score >= 60)
);

// reduce. -> 합쳐진 '결과' (우리가 지정할 수 있음)
let sum = 0;
const data = [1, 3, 5, 7, 9];
for (const v of data) {
  // 하나하나 데이터를 불러와서
  sum += v; // sum로 집어넣음
}
console.log(sum);
console.log("------------");

// 배열.reduce(함수, 초기값) -> 초기값 생략시 배열의 첫째값
console.log(data);
const result = data.reduce((prev, cur) => {
  console.log(prev, cur);
  return prev * cur;
});
console.log(result);
console.log(
  data.reduce(
    (prev, cur) => {
      console.log(prev, cur);
      return prev + cur;
    }, // 화살표 함수
    "" // 문자열
  )
);
console.log(
  data.reduce(
    (prev, cur) => {
      console.log(prev, cur);
      prev[cur] = cur ** 2;
      return prev;
    }, // 화살표 함수
    {} // 객체
  )
);

// sort. 정렬.
const num = [1, 5, 2, 4, 3];
num.sort();
console.log(num); // 오름차순 (ascending) -> 데이터의 등장 순서가 커지는 순서와 일치
num.reverse();
console.log(num);
console.log(num.toSorted()); // 원본에는 영향을 안미치면서...
console.log(num);
console.log(num.toReversed()); // 뒤집기 (가장 큰게 앞으로...)
console.log(num.sort((a, b) => a - b)); // -가 나오면 b가 더 큰 것 -> b가 뒤로 가게 하는 (결과값이 -가 나오면 a, b 중에 b가 뒤로 가게)
console.log(num.sort((a, b) => b - a));
// 내림차순 (descending)
// A -> ... a -> ... 텍스트도 정렬.

const users = [
  {
    name: "kim",
    age: 30,
    job: "programmer",
  },
  {
    name: "lee",
    age: 25,
    job: "designer",
  },
  {
    name: "park",
    age: 35,
    job: "product manager",
  },
  {
    name: "choi",
    age: 28,
    job: "data scientist",
  },
  {
    name: "jung",
    age: 42,
    job: "backend developer",
  },
  {
    name: "kang",
    age: 31,
    job: "frontend developer",
  },
  {
    name: "cho",
    age: 29,
    job: "devops engineer",
  },
  {
    name: "yoon",
    age: 38,
    job: "project manager",
  },
  {
    name: "jang",
    age: 26,
    job: "qa engineer",
  },
  {
    name: "lim",
    age: 33,
    job: "software engineer",
  },
];

// map, filter. reduce.

// 30대 이상의 평균 나이를 구하세요.
const user30 = users
  .map((u) => u.age) // 특정한 속성만 남길 때나 아니면 타입이나 단위. 일괄적인 메서드, 함수등을 적용할 때 많이 씀
  .filter((age) => age >= 30); // 30 이상이 값만 필터링해서 남긴다
const sum30 = user30.reduce((prev, cur) => prev + cur); // 30대 이상의 나이를 합산
const count30 = user30.length;
console.log(sum30 / count30);