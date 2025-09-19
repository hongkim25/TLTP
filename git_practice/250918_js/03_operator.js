// 연산자 -> 1개 이상의 피연산자와 함께 사용해서 계산을 하는 특수기호나 문구

// [산술연산자]
// 사칙연산. 나누기 곱하기 -> 키보드에 없음. -> 엑셀.

// + - * /
console.log("1 + 1 =", 1 + 1);
console.log("2 - 3 =", 2 - 3);
console.log("3 * 4 =", 3 * 4);
console.log("5 / 10 =", 5 / 10); // `//`는 주석이고. / 나누기.
// % **
console.log("100 / 8 =", 100 / 8);
console.log("100 % 8 =", 100 % 8); // 나머지
console.log("2 ** 4 =", 2 ** 4); // 거듭제곱(승)
console.log("2 ** 0.5 =", 2 ** 0.5);
console.log("2 ** (1/3) =", 2 ** (1 / 3));
// ()를 통해서 명시적으로 먼저 계산할 수 있다
// 연산자에는 각각의 우선순위들이 있는데 일반적으로 외우기보다는 ()를 통해서 먼저 계산하게 하면 된다

// 대입(할당)연산자, 복합대입연산자
// =
// 변수에 값을 주입/할당하는 연산자

// [복합 대입 연산자]
// 산술 연산자와 대입 연산자를 결합한 형태의 연산자입니다.
// 코드의 길이를 줄여주고, 가독성을 높여주는 장점이 있습니다.
// 예를 들어, 'a = a + 10;' 와 'a += 10;'은 동일한 작업을 수행합니다.
// 복합 대입 연산자는 내부적으로 더 효율적인 코드를 생성할 수 있으며,
// 특히 변수의 값을 직접 수정(in-place modification)하는 경우에 유용합니다.
// 지원되는 연산자: +=, -=, *=, /=, %=, **=
let a = 10;
a = a + 10;
console.log("a", a);

// a += 10; 와 a = a + 10; 은 같습니다.
a += 10;
console.log("a", a);

// a -= 5; 와 a = a - 5; 는 같습니다.
a -= 5;
console.log("a", a);

// a *= 2; 와 a = a * 2; 는 같습니다.
a *= 2;
console.log("a", a);

// a /= 10; 와 a = a / 10; 은 같습니다.
a /= 10;
console.log("a", a);

// a %= 3; 와 a = a % 3; 은 같습니다.
a %= 3;
console.log("a", a);

// +-*/%**...

// [비교연산자]
// 숫자 (부등호)
console.log("10 > 5", 10 > 5); // 10 > 5 맞다면 true, false
// a > b => 맞으면 true. 틀리면 false
// a >= b, a <= b, a > b, a < b. = 같거나.
console.log('"a" > "b"', "a" > "b");
// ASCII Code. Char. 뒤에 오는 문자들이 더 큰 값.
// 대문자 A : 65 ~ 대문자 Z : 90.
// 소문자 a : 97 ~ 소문자 z : 122.
// 자바스크립트에서는 별도로 변환 없이도 글자 비교 가능
// 동등연산자
// 같다 =. = => 대입연산자. 같은지 여부 -> ==. !=.
console.log("1 == 1", 1 == 1);
console.log("10 == 1", 10 == 1);
console.log("10 != 1", 10 != 1);
console.log('"1" == 1', "1" == 1); // 자동변환을 시켜줍니다
// 자바스크립트의 자동변환 2가지 종류.
// 1) 문자-숫자 간 자동변환
console.log(1 + "1"); // 더하기 연산의 경우 문자와 숫자를 더하기를 시도하면, 문자는 덧셈 연산을 할 경우 연결(concatenate)을 하고, 숫자는 덧셈(add)를 하는데, 덧셈에서는 문자가 이김
const result = 2 + "2";
console.log("result", typeof result);
console.log(1 - "1"); // 덧셈 연산이 아니면...?
console.log(1 * "2");
console.log(1 / "2"); // 숫자로 자동 변환해줍니다
console.log(1 == "1"); // 알아서 변환을 해준거다
// 일치 연산자가 별도로 존재
console.log(1 === "1"); // '자동 변환 없이' 값 자체로 비교하겠다
console.log(1 !== "1"); // ! <- 아니다
console.log(1 / "하하"); // 계산할 수 없는(숫자 변환이 안되는) 문자열과 숫자를 연산 시도하면 NaN(Not A Number)라는 Number 타입이긴한데 유사 오류 취급인 값을 리턴함
console.log(1 / 0); // 에러 안남(?)
// 2) truthy/falsy한 값
// 비어있다, 없다 -> false. [], {}, 0, undefined, null, "", NaN
// 0이 아닌 값. [...]. {...}. "." -> true
console.log("0 == false", 0 == false); // 자동변환 허용시 false 취급 가동
console.log("0 === false", 0 === false); // 자동변환 비허용
// 자바스크립트 자동변환 핵심
console.log("" == false); // 비어있는 류의 데이터는 falsy
console.log("0" == false); // false도 일종의 숫자처럼 판정(false 0, true 1)
console.log("" == "0"); // 문자열간 비교가 되면서 정말 문자들 사이의 비교가 되면서 false.

// 논리연산자
console.log("[논리 연산자]");
// boolean (true/false) -> 합성, 변환
// 벤 다이어그램. 논리 연산 p -> q.
// 연산자 -> 피연산자 1개 이상.
1 + 1; // <피>연산자 몇 개?
// 1개
// typeof // 연산자
console.log(!true); // not 연산자 -> !true -> false, !false -> true!
console.log(!0); // syntactic sugar
// && (and 연산) : ampersand 2개 -> & (1개) : 비트 연산자.
// || (or 연산): pipeline 2개 -> | 비트연산자.

// A && B -> 둘 다 true이면 true
// -> A가 falsy하면 B를 평가하지 않고 A 값으로 값을 취급하고 아닐 경우 B로 취급한다
// A || B -> 둘 다 false이면 false
// -> A가 truthy하면 B를 평가하지 않고 A 값으로 값을 취급하고 아닐 경우 B로 취급한다

console.log("true && true", true && true); // 둘 다 true여야 true다
console.log("true && false", true && false);
console.log("false && true", false && true);
console.log("false && false", false && false);
console.log("10 && 5", 10 && 5);
console.log("10 && false", 10 && false);
console.log("0 && 5", 0 && 5);
console.log("0 && false", 0 && false);
// 앞에가 false로 취급되는 값이면 뒤에 값을 판단하지 않는다

console.log("true || true", true || true); // 둘 다 false여야 false다?
console.log("true || false", true || false);
console.log("false || true", false || true);
console.log("false || false", false || false);
console.log("10 || 5", 10 || 5);
console.log("10 || false", 10 || false);
console.log("0 || 5", 0 || 5);
console.log("0 || false", 0 || false);
// 앞에가 true로 취급되는 값이면 뒤에 값을 판단하지 않는다

// 복합대입연산자 -> 증감연산자
// 변수++, ++변수, 변수--, --변수.
// 변수를 1씩 증가 혹은 감소 시켜주고 바로 대입시켜주는 연산자 -> 바로 변수처럼 쓸 수 있음
let i = 0;
console.log("i++", i++); // 원래는 대입연산자는 이렇게 바로 못씀. 그런데 증감연산자는 바로 써줄 수 있음.
// ++ 혹은 --의 위치가 중요함. 먼저 등장하면 증감 연산을 '마친 상태'에서 사용하고. 뒤에 등장하면 호출한 다음 연산.
console.log("++i", ++i);
false && i++; // 앞이 false면 뒤에까지 가지 않음
console.log(i); // 단축해서.
true || i++; // 앞이 true면 뒤에까지 가지 않음
console.log(i); // 단축해서.

const v = undefined; // 비어있는 데이터
console.log(v || "default"); // v가 비어있으면

// boolean -> 조건문.
// 값을 평가 -> 연산자 1,2개. 피연산자. 3개 연산자. 조건연산자.
// 삼항(조건)연산자.
// condition ? true일 때 : false일 때
// Math.random (0 ~ 1 랜덤 한 값.)
const message = Math.random() > 0.5 ? "당첨입니다" : "꽝입니다";
console.log(message);