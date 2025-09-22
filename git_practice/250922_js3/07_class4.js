// static
// 인스턴스 / 객체 / 메모리 ...
// 클래스 -> 인스턴스 -> 객체
// 인스턴스를 만들지 않아도 클래스 자체에서 사용할 수 있는 프로퍼티나 메서드
// 스태틱 프로퍼티(필드)), 스태틱 메서드. static. static method
// class -> 별도로.

class Utility {
  // 필요할 때마다 생성해서 객체를 만든다면 번거롭겠죠? 전체적으로 사용.
  static setting = "option";
  static helper() {
    console.log("도와줘!");
  }
}

// new 생성자를 사용해서 만들어줘야 기본 프로퍼티라도 만들어지는 건데...
console.log(Utility.setting);
Utility.helper();
// Math.
// Object.