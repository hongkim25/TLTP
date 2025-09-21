/*
Improved app.js with better centering and spacing:
1. Real-time clock
2. Login using localStorage
3. Todo list using localStorage
4. Random background image
5. Geolocation (just shows coordinates, no external weather API)
*/

// Utility selectors
const $ = (sel, root = document) => root.querySelector(sel);
const uid = () => Date.now().toString(36) + Math.random().toString(36).slice(2,8);

// ======= Build UI =======
function buildUI(){
  document.body.style.margin = "0";
  document.body.style.fontFamily = "sans-serif";
  document.body.style.color = "white";
  document.body.style.padding = "20px";
  document.body.style.minHeight = "100vh";
  document.body.style.display = "flex";
  document.body.style.flexDirection = "column";
  document.body.style.alignItems = "center";

  // Header section with clock and login
  const header = document.createElement('div');
  header.style.display = 'flex';
  header.style.flexDirection = 'column';
  header.style.alignItems = 'center';
  header.style.width = '100%';
  header.style.maxWidth = '800px';
  header.style.marginBottom = '40px';
  header.style.gap = '20px';

  const clock = document.createElement('div');
  clock.id = 'clock';
  clock.style.fontSize = '50px';
  clock.style.fontWeight = 'bold';
  header.appendChild(clock);

  const login = document.createElement('div');
  login.id = 'login';
  header.appendChild(login);

  document.body.appendChild(header);

  // Main content container
  const mainContent = document.createElement('div');
  mainContent.style.width = '100%';
  mainContent.style.maxWidth = '600px';
  mainContent.style.textAlign = 'center';

  // Todo section
  const todoSection = document.createElement('div');
  todoSection.style.marginBottom = '30px';
  todoSection.innerHTML = '<h2>Todo List</h2>';
  
  const todoForm = document.createElement('form');
  todoForm.style.marginBottom = '20px';
  todoForm.style.display = 'flex';
  todoForm.style.justifyContent = 'center';
  todoForm.style.gap = '10px';
  
  const todoInput = document.createElement('input');
  todoInput.placeholder = 'Add todo...';
  todoInput.style.padding = '10px';
  todoInput.style.fontSize = '16px';
  todoInput.style.border = 'none';
  todoInput.style.borderRadius = '5px';
  todoInput.style.flex = '1';
  todoInput.style.maxWidth = '300px';
  
  const addBtn = document.createElement('button');
  addBtn.textContent = 'Add';
  addBtn.style.padding = '10px 20px';
  addBtn.style.fontSize = '16px';
  addBtn.style.border = 'none';
  addBtn.style.borderRadius = '5px';
  addBtn.style.backgroundColor = '#007bff';
  addBtn.style.color = 'white';
  addBtn.style.cursor = 'pointer';
  
  todoForm.appendChild(todoInput);
  todoForm.appendChild(addBtn);
  
  const todoList = document.createElement('ul');
  todoList.style.listStyle = 'none';
  todoList.style.padding = '0';
  todoList.style.textAlign = 'left';
  
  todoSection.appendChild(todoForm);
  todoSection.appendChild(todoList);
  mainContent.appendChild(todoSection);

  // Controls section
  const controlsSection = document.createElement('div');
  controlsSection.style.display = 'flex';
  controlsSection.style.gap = '20px';
  controlsSection.style.justifyContent = 'center';
  controlsSection.style.marginBottom = '20px';
  controlsSection.style.flexWrap = 'wrap';

  const bgBtn = document.createElement('button');
  bgBtn.textContent = 'Change Background';
  bgBtn.style.padding = '10px 20px';
  bgBtn.style.fontSize = '16px';
  bgBtn.style.border = 'none';
  bgBtn.style.borderRadius = '5px';
  bgBtn.style.backgroundColor = '#28a745';
  bgBtn.style.color = 'white';
  bgBtn.style.cursor = 'pointer';

  const geoBtn = document.createElement('button');
  geoBtn.textContent = 'Show Location';
  geoBtn.style.padding = '10px 20px';
  geoBtn.style.fontSize = '16px';
  geoBtn.style.border = 'none';
  geoBtn.style.borderRadius = '5px';
  geoBtn.style.backgroundColor = '#ffc107';
  geoBtn.style.color = 'black';
  geoBtn.style.cursor = 'pointer';

  controlsSection.appendChild(bgBtn);
  controlsSection.appendChild(geoBtn);
  mainContent.appendChild(controlsSection);

  const geoDisplay = document.createElement('div');
  geoDisplay.style.marginTop = '20px';
  geoDisplay.style.padding = '10px';
  geoDisplay.style.backgroundColor = 'rgba(0,0,0,0.3)';
  geoDisplay.style.borderRadius = '5px';
  geoDisplay.style.minHeight = '20px';
  mainContent.appendChild(geoDisplay);

  document.body.appendChild(mainContent);

  // Make sure all elements are properly assigned to _app
  window._app = {
    clock: clock,
    login: login, 
    todoInput: todoInput,
    todoList: todoListElement,
    bgBtn: bgBtn,
    geoBtn: geoBtn,
    geoDisplay: geoDisplay
  };
}

// ======= Clock =======
function startClock(){
  const el = _app.clock;
  function tick(){
    const now = new Date();
    el.textContent = now.toLocaleTimeString();
  }
  tick();
  setInterval(tick, 1000);
}

// ======= Login =======
function renderLogin(){
  const container = _app.login;
  container.innerHTML = '';
  const saved = localStorage.getItem('username');
  if(saved){
    const greet = document.createElement('span');
    greet.textContent = `Hello, ${saved}`;
    greet.style.marginRight = '10px';
    greet.style.fontSize = '18px';
    
    const logout = document.createElement('button');
    logout.textContent = 'Logout';
    logout.style.padding = '8px 16px';
    logout.style.fontSize = '14px';
    logout.style.border = 'none';
    logout.style.borderRadius = '5px';
    logout.style.backgroundColor = '#dc3545';
    logout.style.color = 'white';
    logout.style.cursor = 'pointer';
    logout.onclick = () => { localStorage.removeItem('username'); renderLogin(); };
    
    container.appendChild(greet);
    container.appendChild(logout);
  } else {
    const form = document.createElement('form');
    form.style.display = 'flex';
    form.style.gap = '10px';
    form.style.alignItems = 'center';
    
    const input = document.createElement('input');
    input.placeholder = 'Enter name';
    input.style.padding = '8px';
    input.style.fontSize = '16px';
    input.style.border = 'none';
    input.style.borderRadius = '5px';
    
    const btn = document.createElement('button');
    btn.textContent = 'Login';
    btn.style.padding = '8px 16px';
    btn.style.fontSize = '16px';
    btn.style.border = 'none';
    btn.style.borderRadius = '5px';
    btn.style.backgroundColor = '#007bff';
    btn.style.color = 'white';
    btn.style.cursor = 'pointer';
    
    form.appendChild(input);
    form.appendChild(btn);
    form.onsubmit = e => {
      e.preventDefault();
      if(input.value.trim()){ localStorage.setItem('username', input.value.trim()); renderLogin(); }
    };
    container.appendChild(form);
  }
}

// ======= Todo =======
function loadTodos(){
  const todos = JSON.parse(localStorage.getItem('todos')||'[]');
  renderTodos(todos);
}
function renderTodos(todos){
  _app.todoList.innerHTML = '';
  if(todos.length===0){
    const li = document.createElement('li');
    li.textContent = 'No todos yet';
    li.style.textAlign = 'center';
    li.style.color = '#ccc';
    li.style.fontStyle = 'italic';
    li.style.padding = '20px';
    _app.todoList.appendChild(li);
    return;
  }
  todos.forEach(t=>{
    const li = document.createElement('li');
    li.style.display = 'flex';
    li.style.alignItems = 'center';
    li.style.padding = '10px';
    li.style.backgroundColor = 'rgba(255,255,255,0.1)';
    li.style.marginBottom = '5px';
    li.style.borderRadius = '5px';
    li.style.gap = '10px';
    
    const chk = document.createElement('input');
    chk.type = 'checkbox';
    chk.checked = t.done;
    chk.onchange = ()=> toggleTodo(t.id);
    chk.style.transform = 'scale(1.2)';
    
    const span = document.createElement('span');
    span.textContent = t.text;
    span.style.flex = '1';
    if(t.done) {
      span.style.textDecoration = 'line-through';
      span.style.opacity = '0.6';
    }
    
    const del = document.createElement('button');
    del.textContent = 'Delete';
    del.style.padding = '5px 10px';
    del.style.fontSize = '12px';
    del.style.border = 'none';
    del.style.borderRadius = '3px';
    del.style.backgroundColor = '#dc3545';
    del.style.color = 'white';
    del.style.cursor = 'pointer';
    del.onclick = ()=> deleteTodo(t.id);
    
    li.appendChild(chk);
    li.appendChild(span);
    li.appendChild(del);
    _app.todoList.appendChild(li);
  });
}
function addTodo(text){
  const todosArray = JSON.parse(localStorage.getItem('todos')||'[]');
  todosArray.push({id: uid(), text, done:false});
  localStorage.setItem('todos', JSON.stringify(todosArray));
  renderTodos(todosArray);
}
function toggleTodo(id){
  const todosArray = JSON.parse(localStorage.getItem('todos')||'[]');
  const t = todosArray.find(t=>t.id===id);
  if(t){ t.done=!t.done; localStorage.setItem('todos', JSON.stringify(todosArray)); renderTodos(todosArray); }
}
function deleteTodo(id){
  let todosArray = JSON.parse(localStorage.getItem('todos')||'[]');
  todosArray = todosArray.filter(t=>t.id!==id);
  localStorage.setItem('todos', JSON.stringify(todosArray));
  renderTodos(todosArray);
}

// ======= Background =======
function setRandomBackground(){
  // Using a more reliable background service
  const queries = ['nature','city','forest','sky','sea','mountain','ocean','sunset'];
  const q = queries[Math.floor(Math.random()*queries.length)];
  const randomId = Math.floor(Math.random() * 1000) + 1;
  
  // Try Unsplash first, fallback to solid colors if it fails
  const imageUrl = `https://picsum.photos/1600/900?random=${randomId}`;
  
  // Create a new image to test if it loads
  const img = new Image();
  img.onload = function() {
    document.body.style.backgroundImage = `url(${imageUrl})`;
    document.body.style.backgroundSize = 'cover';
    document.body.style.backgroundPosition = 'center';
    document.body.style.backgroundRepeat = 'no-repeat';
  };
  img.onerror = function() {
    // Fallback to gradient backgrounds if image fails
    const gradients = [
      'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
    ];
    const gradient = gradients[Math.floor(Math.random() * gradients.length)];
    document.body.style.backgroundImage = gradient;
    document.body.style.backgroundSize = 'cover';
  };
  img.src = imageUrl;
}

// ======= Geolocation & Weather =======
async function showLocationAndWeather(){
  const geoDisplay = _app.geoDisplay;
  
  if(!navigator.geolocation){
    geoDisplay.innerHTML = '<div style="color: #ff6b6b;">❌ Geolocation not supported.</div>';
    return;
  }
  
  geoDisplay.innerHTML = '<div style="color: #ffd93d;">🔍 Getting your location and weather...</div>';
  
  navigator.geolocation.getCurrentPosition(async (pos) => {
    const {latitude, longitude} = pos.coords;
    
    try {
      // Option 1: Free weather API (no registration needed)
      // Using wttr.in - completely free, no API key required
      const weatherUrl = `https://wttr.in/${latitude},${longitude}?format=j1`;
      
      console.log('Fetching weather from wttr.in:', weatherUrl);
      
      const response = await fetch(weatherUrl);
      
      if (!response.ok) {
        throw new Error('Weather service unavailable');
      }
      
      const weatherData = await response.json();
      console.log('Weather data from wttr.in:', weatherData);
      
      const current = weatherData.current_condition[0];
      const location = weatherData.nearest_area[0];
      
      const temp = current.temp_C;
      const feelsLike = current.FeelsLikeC;
      const humidity = current.humidity;
      const description = current.weatherDesc[0].value;
      const windSpeed = current.windspeedKmph;
      const locationName = location.areaName[0].value;
      const country = location.country[0].value;
      
      // Map weather codes to emojis
      const weatherEmoji = getWeatherEmoji(current.weatherCode);
      
      geoDisplay.innerHTML = `
        <div style="text-align: center; background: rgba(255,255,255,0.1); padding: 20px; border-radius: 10px;">
          <div style="font-size: 24px; font-weight: bold; margin-bottom: 10px;">
            📍 ${locationName}, ${country}
          </div>
          <div style="font-size: 18px; color: #ddd; margin-bottom: 15px;">
            위도: ${latitude.toFixed(4)}, 경도: ${longitude.toFixed(4)}
          </div>
          <div style="display: flex; align-items: center; justify-content: center; gap: 15px; margin-bottom: 15px;">
            <div style="font-size: 60px;">${weatherEmoji}</div>
            <div style="text-align: left;">
              <div style="font-size: 32px; font-weight: bold; color: #4ecdc4;">${temp}°C</div>
              <div style="font-size: 16px; color: #ddd;">체감 ${feelsLike}°C</div>
            </div>
          </div>
          <div style="background: rgba(0,0,0,0.2); padding: 15px; border-radius: 8px; margin-bottom: 10px;">
            <div style="font-size: 18px; color: #ffd93d; margin-bottom: 8px;">${description}</div>
            <div style="display: flex; justify-content: space-around; font-size: 14px; color: #bbb;">
              <span>💧 습도: ${humidity}%</span>
              <span>💨 바람: ${windSpeed}km/h</span>
            </div>
          </div>
          <div style="font-size: 12px; color: #999;">
            마지막 업데이트: ${new Date().toLocaleString('ko-KR')}
          </div>
          <div style="font-size: 11px; color: #666; margin-top: 5px;">
            ⚡ Powered by wttr.in (Free Weather Service)
          </div>
        </div>
      `;
      
    } catch (error) {
      console.error('Weather fetch error:', error);
      
      // Fallback: Try a different free API or show location only
      try {
        // Fallback option: Use a reverse geocoding service to at least get location name
        const geoUrl = `https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=${latitude}&longitude=${longitude}&localityLanguage=ko`;
        const geoResponse = await fetch(geoUrl);
        const geoData = await geoResponse.json();
        
        geoDisplay.innerHTML = `
          <div style="text-align: center; background: rgba(255,255,255,0.1); padding: 15px; border-radius: 8px;">
            <div style="font-size: 20px; margin-bottom: 10px;">📍 현재 위치</div>
            <div style="font-size: 16px; color: #4ecdc4; margin-bottom: 8px;">
              ${geoData.city || geoData.locality || '알 수 없는 도시'}, ${geoData.countryName || 'Korea'}
            </div>
            <div style="color: #ddd; margin-bottom: 10px;">
              위도: ${latitude.toFixed(4)}, 경도: ${longitude.toFixed(4)}
            </div>
            <div style="font-size: 12px; color: #ff6b6b;">
              ⚠️ 날씨 정보 일시 불가 (${error.message})
            </div>
          </div>
        `;
        
      } catch (fallbackError) {
        // Final fallback: coordinates only
        geoDisplay.innerHTML = `
          <div style="text-align: center; background: rgba(255,255,255,0.1); padding: 15px; border-radius: 8px;">
            <div style="font-size: 18px; margin-bottom: 10px;">📍 현재 위치</div>
            <div style="color: #4ecdc4;">위도: ${latitude.toFixed(4)}</div>
            <div style="color: #4ecdc4;">경도: ${longitude.toFixed(4)}</div>
            <div style="font-size: 12px; color: #ff6b6b; margin-top: 10px;">
              ⚠️ 위치명과 날씨 정보를 가져올 수 없습니다
            </div>
          </div>
        `;
      }
    }
    
  }, (error) => {
    console.error('Geolocation error:', error);
    let errorMessage = '위치 정보를 가져올 수 없습니다.';
    
    switch(error.code) {
      case error.PERMISSION_DENIED:
        errorMessage = '위치 접근 권한이 거부되었습니다. 브라우저 설정에서 위치 접근을 허용해주세요.';
        break;
      case error.POSITION_UNAVAILABLE:
        errorMessage = '위치 정보를 사용할 수 없습니다.';
        break;
      case error.TIMEOUT:
        errorMessage = '위치 정보 요청이 시간 초과되었습니다.';
        break;
    }
    
    geoDisplay.innerHTML = `<div style="color: #ff6b6b; text-align: center;">❌ ${errorMessage}</div>`;
  });
}

// Helper function to convert weather codes to emojis
function getWeatherEmoji(code) {
  const weatherCodes = {
    '113': '☀️', // Sunny
    '116': '⛅', // Partly cloudy
    '119': '☁️', // Cloudy
    '122': '☁️', // Overcast
    '143': '🌫️', // Mist
    '176': '🌦️', // Patchy rain possible
    '179': '🌨️', // Patchy snow possible
    '182': '🌨️', // Patchy sleet possible
    '185': '🌨️', // Patchy freezing drizzle possible
    '200': '⛈️', // Thundery outbreaks possible
    '227': '❄️', // Blowing snow
    '230': '❄️', // Blizzard
    '248': '🌫️', // Fog
    '260': '🌫️', // Freezing fog
    '263': '🌦️', // Patchy light drizzle
    '266': '🌧️', // Light drizzle
    '281': '🌨️', // Freezing drizzle
    '284': '🌨️', // Heavy freezing drizzle
    '293': '🌦️', // Patchy light rain
    '296': '🌧️', // Light rain
    '299': '🌧️', // Moderate rain at times
    '302': '🌧️', // Moderate rain
    '305': '🌧️', // Heavy rain at times
    '308': '🌧️', // Heavy rain
    '311': '🌨️', // Light freezing rain
    '314': '🌨️', // Moderate or heavy freezing rain
    '317': '🌨️', // Light sleet
    '320': '🌨️', // Moderate or heavy sleet
    '323': '🌨️', // Patchy light snow
    '326': '❄️', // Light snow
    '329': '❄️', // Patchy moderate snow
    '332': '❄️', // Moderate snow
    '335': '❄️', // Patchy heavy snow
    '338': '❄️', // Heavy snow
    '350': '🌨️', // Ice pellets
    '353': '🌦️', // Light rain shower
    '356': '🌧️', // Moderate or heavy rain shower
    '359': '🌧️', // Torrential rain shower
    '362': '🌨️', // Light sleet showers
    '365': '🌨️', // Moderate or heavy sleet showers
    '368': '🌨️', // Light snow showers
    '371': '❄️', // Moderate or heavy snow showers
    '374': '🌨️', // Light showers of ice pellets
    '377': '🌨️', // Moderate or heavy showers of ice pellets
    '386': '⛈️', // Patchy light rain with thunder
    '389': '⛈️', // Moderate or heavy rain with thunder
    '392': '⛈️', // Patchy light snow with thunder
    '395': '⛈️', // Moderate or heavy snow with thunder
  };
  
  return weatherCodes[code] || '🌤️'; // Default emoji
}

// ======= Init =======
window.onload = ()=>{
  buildUI();
  startClock();
  renderLogin();
  loadTodos();
  _app.todoInput.form.onsubmit = e=>{
    e.preventDefault();
    const text = _app.todoInput.value.trim();
    if(text){ addTodo(text); _app.todoInput.value=''; }
  };
  _app.bgBtn.onclick = setRandomBackground;
  _app.geoBtn.onclick = showLocationAndWeather;
  setRandomBackground();
};