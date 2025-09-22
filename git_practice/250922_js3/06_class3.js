// 접근자
// [] -> 키. + = 대입연산자 주입.
// ?? -> 대입연산자 -> ????
// accessor / access. / validation.
// validate. 타당.
// 이름 name -> 빈 문자열(텍스트) -> let name = "" <= X.
// 함수. 메서드화를 하면. 무시해버리거나, 에러처리를 해버리거나, ...
// getter setter
class Rectangle {
  // width, height
  constructor(width, height) {
    this._width = width; // 가로
    this._height = height; // 세로
  }
  // getter
  get width() {
    return this._width;
  }
  // setter
  set width(newWidth) {
    if (typeof newWidth != "number") {
      console.log("숫자가 아닌 값을 입력할 수 없습니다!");
      return;
    }
    if (newWidth < 0) {
      console.log("0보다 작게 입력할 수 없습니다!");
      return;
    }
    this._width = newWidth;
  }

  get height() {
    return this._height;
  }
  set height(newHeight) {
    this._height = newHeight;
  }
  get size() {
    return this._width * this._height;
  }
}

const r = new Rectangle(100, 100);
r.width = 100; // 함수인데 프로퍼티 값을 다루는 것처럼 취급시킬 수 있다
r.width = "ff";
console.log(r.size);

// static

// 예외처리 -> 별도파일.