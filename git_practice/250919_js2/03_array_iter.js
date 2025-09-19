const arr = [10, 20, 30, 40, 50]; // 전체 인덱스는 0으로 시작해서 전체 길이 - 1.

// index와 매칭을 위해 0으로 시작.
for (let i = 0; i < arr.length; i++) {
  // 전체를 탐색한다
  console.log(i, arr[i]);
}

const arr2 = [];
for (let i = 0; i < 10; i++) {
  // 10개까지 새롭게 추가하는데...
  arr2.push((i + 1) * 9); // i번째에 인덱스에 1을 더하고 9를 곱한 값을 추가해주겠다
} // 새롭게 추가할 경우에는 length가 없으므로 내가 새롭게 넣어줘야하고요
console.log(arr2);

console.log(arr);
for (let i = 0; i < arr.length; i++) {
  // 조회하거나 수정한다면 기존의 length를 활용가능
  arr[i] /= 100; // 나누기 100을 한 값을 대입하겠다
}
console.log(arr);

// for of. -> index를 사용하지 않고 array를 자체를 탐색하는 경우
console.log(arr);
// for (let v of arr) {
for (const v of arr) {
  // v -> 한번의 반복에만 존재하고 소멸함.
  console.log(v); // 별도로 arr[i]하지 않아도...
  //   v += 10; // 영향이 없다
  // 혹시나 헷갈릴까봐 아예 const로 하라고 권장.
}
for (let i = 0; i < 10; i++) {
  console.log(i);
  i += 10; // 영향 받음
}

// 이중 배열과 별찍기

// 표, 행렬. 좌표.
// *
// **
// ***
// ****
// *****
console.log("*");
console.log("**");
console.log("***");
console.log("****");
console.log("*****");

for (let i = 1; i <= 5; i++) {
  console.log("*".repeat(i));
}

const matrix = [];
for (let i = 0; i < 5; i++) {
  // 가로를 의미하는 축 (행)
  const row = [];
  for (let j = 0; j < 5; j++) {
    // 세로를 의미하는 축
    row.push("*"); // 열.
  }
  matrix.push(row);
}
console.log(matrix);