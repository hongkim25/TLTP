// 배열 (Array) <- 자료구조 (Data Structure)
// 1. 동일한 타입인 0개 이상의 데이터의 묶음
// 2. 인덱스(index)를 사용하여 순서를 참조할 수 있음
// 3. 길이는 최초에 메모리에 배정하여 선언한 길이에 고정적임
// 자바스크립트는 여기서 따라가는 건 딱 2번밖에 없음

// 자바스크립트 배열 (JavaScript Array)
// 1. 0개 이상의 데이터 묶음 (동일한 타입일 필요 없음)
// 2. '숫자'로 되어 있는 키의 경우 인덱스로 분류되어 사용될 수 있음
// 3. 길이는 유동적이며 심지어 외부에서 변화시킬 수 있는 값임.

// 데이터를 변수 -> 1개씩 -> 비슷한 역할을 하는 데이터들을 묶어서.
// 1) 순서가 있는 경우 2) 순서가 없는 경우
// 배열(Array)

// []
console.log([1, 2, 3, 4, 5]); // []
// 배열 -> const. 배열 자체는 다시 재할당 x -> 내부에 요소(원소)들을 다시 변경 -> const
const arr = [1, 3, 5, 7, 9];
console.log(arr);
// 빈 배열
const empty = []; // 빈 배열
// boolean -> false.

// C.R.U.D
// 일반적으로 데이터 다루기. -> C.R.U.D
// Create -> 추가.생성
// Read -> 조회.불러오기
// Update -> 수정.덮어씌우기
// Delete -> 삭제
// 배열.push(추가할 데이터)
const numArr = [1, 1, 1];
numArr.push(10); // 맨 끝에 추가 ✅
console.log("numArr", numArr);
numArr.unshift(100); // 맨 앞에 추가
console.log("numArr", numArr);
// 특정한 값을 읽어와주고 싶으면?
// 인덱스
// numArr [ 100, 1, 1, 1, 10 ]
// 배열 -> 0부터 시작함. [0] [1] ... [전체길이 - 1]
console.log("numArr.length", numArr.length);
console.log("numArr[0]", numArr[0]); // 배열명[인덱스]

// const로 해놓는 건 실수로라도 재할당을 해서 전체적으로 덮어씌우는 걸 방지하기 위함
// react나 등등 상태관리에서 mutate 감지 -> property 변경을 일으키는 걸 위해서 권장하는 것도 있음.

numArr.splice(2, 0, 64); // 2번째 인덱스에 0개 삭제 후 64를 넣겠다
console.log("numArr", numArr);

console.log(numArr.slice(3, 5)); // 끝점 포함 X

numArr[3] = 32;
console.log(numArr);

console.log(numArr.pop());
console.log(numArr);
console.log(numArr.shift()); // 맨앞에 추가 unshift <-> 삭제 shift
console.log(numArr);

numArr.splice(1, 2);
console.log(numArr);
delete numArr[0];
console.log(numArr);

// push?
const arr2 = ["a", "b", "c", "d"];
console.log(arr2.length);
arr2.length = 1;
console.log(arr2.length);
console.log(arr2);
arr2.length = 4;
console.log(arr2.length);
console.log(arr2);
const arr3 = [];
arr3.length = 100;
console.log(arr3);
// 자바스크립트에서는 별도의 'clear'는 없는데... 그냥 length를 0으로 만들면 자리가 0으로 되면서 해당 데이터도 지워지는?
arr3.length = 0;
console.log(arr3[10]); // undefined