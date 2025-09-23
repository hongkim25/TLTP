import React, { useState } from 'react';
import './App.css';

function App() {
  const [displayValue, setDisplayValue] = useState('0');
  const [previousValue, setPreviousValue] = useState(null);
  const [operator, setOperator] = useState(null);
  const [waitingForOperand, setWaitingForOperand] = useState(false);

  const handleNumberClick = (numStr) => {
    if (waitingForOperand) {
      setDisplayValue(numStr);
      setWaitingForOperand(false);
    } else {
      setDisplayValue(displayValue === '0' ? numStr : displayValue + numStr);
    }
  };

  const handleOperatorClick = (nextOperator) => {
    const inputValue = parseFloat(displayValue);

    if (previousValue === null) {
      setPreviousValue(inputValue);
    } else if (operator) {
      const result = calculate(previousValue, inputValue, operator);
      setPreviousValue(result);
      setDisplayValue(String(result));
    }

    setWaitingForOperand(true);
    setOperator(nextOperator);
  };

  const handleEqualClick = () => {
    if (!operator || previousValue === null) return;

    const currentValue = parseFloat(displayValue);
    const result = calculate(previousValue, currentValue, operator);

    setDisplayValue(String(result));
    setPreviousValue(null);
    setOperator(null);
    setWaitingForOperand(true);
  };

  const handleClearClick = () => {
    setDisplayValue('0');
    setPreviousValue(null);
    setOperator(null);
    setWaitingForOperand(false);
  };

  const handleDecimalClick = () => {
    if (waitingForOperand) {
      setDisplayValue('0.');
      setWaitingForOperand(false);
      return;
    }
    if (!displayValue.includes('.')) {
      setDisplayValue(displayValue + '.');
    }
  };

  const calculate = (prev, current, op) => {
    switch (op) {
      case '+': return prev + current;
      case '-': return prev - current;
      case '×': return prev * current;
      case '÷': return prev / current;
      default: return current;
    }
  };

  return (
    <div className="calculator">
      <div className="display">{displayValue}</div>
      <div className="button-panel">
        <button onClick={handleClearClick} className="button clear">C</button>
        <button onClick={() => handleOperatorClick('÷')} className="button operator">÷</button>
        <button onClick={() => handleOperatorClick('×')} className="button operator">×</button>

        <button onClick={() => handleNumberClick('7')} className="button">7</button>
        <button onClick={() => handleNumberClick('8')} className="button">8</button>
        <button onClick={() => handleNumberClick('9')} className="button">9</button>
        <button onClick={() => handleOperatorClick('-')} className="button operator">-</button>

        <button onClick={() => handleNumberClick('4')} className="button">4</button>
        <button onClick={() => handleNumberClick('5')} className="button">5</button>
        <button onClick={() => handleNumberClick('6')} className="button">6</button>
        <button onClick={() => handleOperatorClick('+')} className="button operator">+</button>

        <button onClick={() => handleNumberClick('1')} className="button">1</button>
        <button onClick={() => handleNumberClick('2')} className="button">2</button>
        <button onClick={() => handleNumberClick('3')} className="button">3</button>
        <button onClick={handleEqualClick} className="button operator" style={{gridRow: 'span 2'}}>=</button>

        <button onClick={() => handleNumberClick('0')} className="button zero">0</button>
        <button onClick={handleDecimalClick} className="button">.</button>
      </div>
    </div>
  );
}

export default App;
