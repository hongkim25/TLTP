// 객체
// 1. {} 중괄호 -> 정의
const obj1 = {
  name: "kim",
  hello: function () {
    console.log("안녕하세요!");
    console.log(`저의 이름은 ${this.name}입니다!`);
  },
};
console.log(obj1.name);
obj1.hello();
// 2. 프로토타입
function Person(name) {
  this.name = name;
  // this를 사용해서 속성을 주입해서 만드는 '생성자 함수'
}
const p = new Person("김철수");
console.log(p.name);
const p2 = new Person("박철수");
console.log(p2.name);
Person.prototype.hello = function () {
  console.log("안녕! " + this.name);
};
p2.hello();
p.hello();
// 구식 문법이라 자세하게 알 필요는 없다 (프로토타입)

// JS, Java, ...
// 다 하나다. Java. Spring. OOP. 객체 지향 프로그래밍.
// Class -> JS -> 추가. -> 프로토타입 -> Class

// 클래스. - Java 클래스.
// 1. JS. 접근제어자. -> 자바. 특정한 속성, 메서드(함수) -> 임의 외부의 접근을 원천적으로 막을 수 있음 -> 내부에서 알아서 작업을 한 뒤 -> 외부에서 특정한 통로로만 호출할 수 있게...
// 같은 파일이면은 원칙으로 다 접근할 수 있다 (모듈화 한 다음엔 private의 경우엔 일부 작동)
// 2. 오버로딩이 없음. -> 매개변수의 다른 조합에 따른 같은 이름의 메서드/함수 돌려쓰기가 없다. -> 생성자가 하나다.
// 3. 상속은 있는데 구현. interface. extends는 존재하는데 implements. (typescript interface vs java interface)

class User {
  // 객체/참조 타입

  // 프로퍼티 (필드)
  // 프로퍼티명 = 값
  job = "programmer";

  // 생성자
  constructor(name) {
    // this.프로퍼티명 -> 클래스 설계를 통해 만들 객체가 가질 프로퍼티명
    // this.name = "kim";
    this.name = name || "kim"; // 생성자의 매개변수를 통해서 프로퍼티랑 연결
    // || "kim" : 단축 연산을 통한 디폴트 연산
  }

  // 메서드 - 객체에 묶여서 같이 행동 -> 메서드. -> 함수와 동일.
  sayHi() {
    // this가 있다 + 클래스에 속한다 -> 나머지 다 함수와 동일.
    console.log("HI!");
    console.log(this.name);
    console.log(this.job);
  }
}

const user1 = new User();
console.log(user1);
console.log(user1.name);
const user2 = new User("park");
console.log(user2.name);
user2.sayHi();