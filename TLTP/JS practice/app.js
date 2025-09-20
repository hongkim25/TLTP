/*
app.js
Single-file vanilla JS app that creates a simple web UI and implements:
- Real-time clock
- Login using localStorage (simple username)
- To-do list stored in localStorage (add / toggle done / delete)
- Random background image (Unsplash source)
- Weather & location via Geolocation + OpenWeatherMap

USAGE
1) Create an empty HTML file (index.html) with a <body> and include this script at the end:
   <script src="app.js"></script>
2) Sign up for an OpenWeatherMap API key and paste it into the API_KEY variable below.
   If you don't have an API key, the weather widget will gracefully show an instruction.

Notes
- This file builds its own UI (no separate CSS required). It uses simple, responsive inline styles.
- All persistent state is kept in localStorage keys: 'app_username' and 'app_todos'.
*/

// ======= CONFIG =======
const API_KEY = "YOUR_OPENWEATHERMAP_API_KEY_HERE"; // <-- paste your key here
const BG_QUERY = ["nature","landscape","city","sky","mountain","sea","forest","street","architecture"];

// ======= Utilities =======
const $ = (sel, root = document) => root.querySelector(sel);
const qs = (sel, root = document) => Array.from(root.querySelectorAll(sel));
const uid = () => Date.now().toString(36) + Math.random().toString(36).slice(2,8);

// ======= Local Storage Helpers =======
const LS = {
  getUser() { return localStorage.getItem('app_username'); },
  setUser(name) { localStorage.setItem('app_username', name); },
  clearUser() { localStorage.removeItem('app_username'); },
  getTodos() {
    try { return JSON.parse(localStorage.getItem('app_todos') || '[]'); }
    catch(e){ return []; }
  },
  saveTodos(todos) { localStorage.setItem('app_todos', JSON.stringify(todos)); }
};

// ======= Build UI =======
function buildUI(){
  document.body.style.margin = "0";
  document.body.style.fontFamily = "Inter, system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial";

  // container
  const root = document.createElement('div');
  root.id = 'app-root';
  root.style.minHeight = '100vh';
  root.style.display = 'flex';
  root.style.flexDirection = 'column';
  root.style.backdropFilter = 'blur(0px)';
  root.style.backgroundColor = 'rgba(255,255,255,0.03)';
  root.style.color = '#fff';
  root.style.padding = '24px';
  root.style.boxSizing = 'border-box';

  // top bar: clock + login
  const topBar = document.createElement('div');
  topBar.style.display = 'flex';
  topBar.style.justifyContent = 'space-between';
  topBar.style.alignItems = 'center';
  topBar.style.gap = '16px';

  const clock = document.createElement('div');
  clock.id = 'rtc-clock';
  clock.style.fontSize = '18px';
  clock.style.fontWeight = '600';
  clock.style.letterSpacing = '0.4px';

  const loginWrap = document.createElement('div');
  loginWrap.id = 'login-wrap';
  loginWrap.style.display = 'flex';
  loginWrap.style.alignItems = 'center';
  loginWrap.style.gap = '8px';

  topBar.appendChild(clock);
  topBar.appendChild(loginWrap);

  // main content grid
  const grid = document.createElement('div');
  grid.style.display = 'grid';
  grid.style.gridTemplateColumns = 'minmax(260px, 380px) 1fr';
  grid.style.gap = '20px';
  grid.style.marginTop = '18px';

  // left column: todos + change bg
  const left = document.createElement('div');
  left.style.display = 'flex';
  left.style.flexDirection = 'column';
  left.style.gap = '12px';

  // todo card
  const todoCard = card('Todo List');
  todoCard.style.padding = '12px';

  const todoForm = document.createElement('form');
  todoForm.style.display = 'flex';
  todoForm.style.gap = '8px';
  todoForm.onsubmit = (e) => { e.preventDefault(); addTodoInput(); };

  const todoInput = document.createElement('input');
  todoInput.placeholder = 'Add todo...';
  todoInput.style.flex = '1';
  todoInput.style.padding = '8px';
  todoInput.style.borderRadius = '8px';
  todoInput.style.border = 'none';
  todoInput.style.outline = 'none';

  const addBtn = document.createElement('button');
  addBtn.type = 'submit';
  addBtn.textContent = 'Add';
  styleButton(addBtn);

  todoForm.appendChild(todoInput);
  todoForm.appendChild(addBtn);

  const todoList = document.createElement('div');
  todoList.id = 'todo-list';
  todoList.style.display = 'flex';
  todoList.style.flexDirection = 'column';
  todoList.style.gap = '8px';
  todoList.style.marginTop = '12px';

  todoCard.content.appendChild(todoForm);
  todoCard.content.appendChild(todoList);

  // random bg card
  const bgCard = card('Background');
  bgCard.style.padding = '12px';
  const bgInfo = document.createElement('div');
  bgInfo.textContent = 'Random background from Unsplash. Click to change.';
  bgInfo.style.marginBottom = '8px';
  const bgBtn = document.createElement('button');
  bgBtn.textContent = 'Change Background';
  styleButton(bgBtn);
  bgBtn.onclick = setRandomBackground;
  bgCard.content.appendChild(bgInfo);
  bgCard.content.appendChild(bgBtn);

  left.appendChild(todoCard.card);
  left.appendChild(bgCard.card);

  // right column: weather + extra
  const right = document.createElement('div');
  right.style.display = 'flex';
  right.style.flexDirection = 'column';
  right.style.gap = '12px';

  const weatherCard = card('Weather & Location');
  weatherCard.style.padding = '12px';

  const weatherInfo = document.createElement('div');
  weatherInfo.id = 'weather-info';
  weatherInfo.textContent = 'Click "Detect location" to fetch weather.';
  weatherInfo.style.marginBottom = '12px';

  const detectBtn = document.createElement('button');
  detectBtn.textContent = 'Detect location';
  styleButton(detectBtn);
  detectBtn.onclick = detectLocationAndWeather;

  weatherCard.content.appendChild(weatherInfo);
  weatherCard.content.appendChild(detectBtn);

  right.appendChild(weatherCard.card);

  grid.appendChild(left);
  grid.appendChild(right);

  // footer small note
  const foot = document.createElement('div');
  foot.style.marginTop = '18px';
  foot.style.fontSize = '12px';
  foot.style.opacity = '0.85';
  foot.textContent = 'Local-only demo. Data stored in your browser.';

  root.appendChild(topBar);
  root.appendChild(grid);
  root.appendChild(foot);

  document.body.appendChild(root);

  // store references on window for convenience
  window._app = { root, clock, loginWrap, todoInput, todoList, weatherInfo };
}

function card(title){
  const wrapper = document.createElement('div');
  wrapper.style.background = 'linear-gradient(180deg, rgba(255,255,255,0.04), rgba(255,255,255,0.02))';
  wrapper.style.borderRadius = '12px';
  wrapper.style.padding = '8px';
  wrapper.style.boxShadow = '0 6px 18px rgba(0,0,0,0.35)';
  wrapper.style.backdropFilter = 'blur(6px)';

  const head = document.createElement('div');
  head.style.display = 'flex';
  head.style.justifyContent = 'space-between';
  head.style.alignItems = 'center';

  const h = document.createElement('div');
  h.textContent = title;
  h.style.fontWeight = '700';
  h.style.fontSize = '15px';

  head.appendChild(h);

  const content = document.createElement('div');
  content.style.marginTop = '10px';

  wrapper.appendChild(head);
  wrapper.appendChild(content);

  return { card: wrapper, content };
}

function styleButton(btn){
  btn.style.padding = '8px 10px';
  btn.style.borderRadius = '8px';
  btn.style.border = 'none';
  btn.style.cursor = 'pointer';
  btn.style.background = 'rgba(255,255,255,0.07)';
  btn.style.color = '#fff';
  btn.style.fontWeight = '600';
}

// ======= Clock =======
function startClock(){
  const el = window._app.clock;
  function tick(){
    const now = new Date();
    const hh = String(now.getHours()).padStart(2,'0');
    const mm = String(now.getMinutes()).padStart(2,'0');
    const ss = String(now.getSeconds()).padStart(2,'0');
    el.textContent = `${hh}:${mm}:${ss}`;
  }
  tick();
  setInterval(tick, 1000);
}

// ======= Login =======
function renderLogin(){
  const wrap = window._app.loginWrap;
  wrap.innerHTML = '';
  const user = LS.getUser();
  if(user){
    const greet = document.createElement('div');
    greet.textContent = `Hello, ${user}`;
    greet.style.fontWeight = '600';

    const out = document.createElement('button');
    out.textContent = 'Logout';
    styleButton(out);
    out.onclick = () => { LS.clearUser(); renderLogin(); };

    wrap.appendChild(greet);
    wrap.appendChild(out);
  } else {
    const form = document.createElement('form');
    form.style.display = 'flex';
    form.style.gap = '8px';
    form.onsubmit = (e) => { e.preventDefault(); const name = inp.value.trim(); if(name){ LS.setUser(name); renderLogin(); } };

    const inp = document.createElement('input');
    inp.placeholder = 'Your name';
    inp.style.padding = '6px';
    inp.style.borderRadius = '8px';
    inp.style.border = 'none';
    inp.style.outline = 'none';

    const btn = document.createElement('button');
    btn.type = 'submit';
    btn.textContent = 'Login';
    styleButton(btn);

    form.appendChild(inp);
    form.appendChild(btn);
    wrap.appendChild(form);
  }
}

// ======= Todos =======
function loadTodos(){
  const todos = LS.getTodos();
  renderTodos(todos);
}

function renderTodos(todos){
  const list = window._app.todoList;
  list.innerHTML = '';
  if(todos.length === 0){
    const p = document.createElement('div');
    p.textContent = 'No todos yet.';
    p.style.opacity = '0.9';
    list.appendChild(p);
    return;
  }
  todos.forEach(t => {
    const row = document.createElement('div');
    row.style.display = 'flex';
    row.style.alignItems = 'center';
    row.style.justifyContent = 'space-between';
    row.style.gap = '8px';

    const left = document.createElement('div');
    left.style.display = 'flex';
    left.style.alignItems = 'center';
    left.style.gap = '8px';

    const chk = document.createElement('input');
    chk.type = 'checkbox';
    chk.checked = !!t.done;
    chk.onchange = () => toggleTodo(t.id);

    const txt = document.createElement('div');
    txt.textContent = t.text;
    txt.style.textDecoration = t.done ? 'line-through' : 'none';
    txt.style.opacity = t.done ? '0.6' : '0.95';

    left.appendChild(chk);
    left.appendChild(txt);

    const del = document.createElement('button');
    del.textContent = 'Delete';
    styleButton(del);
    del.onclick = () => deleteTodo(t.id);

    row.appendChild(left);
    row.appendChild(del);

    list.appendChild(row);
  });
}

function addTodoInput(){
  const input = window._app.todoInput;
  const text = input.value.trim();
  if(!text) return;
  const todos = LS.getTodos();
  todos.unshift({ id: uid(), text, done: false });
  LS.saveTodos(todos);
  input.value = '';
  renderTodos(todos);
}

function toggleTodo(id){
  const todos = LS.getTodos();
  const idx = todos.findIndex(t => t.id === id);
  if(idx >= 0){ todos[idx].done = !todos[idx].done; LS.saveTodos(todos); renderTodos(todos); }
}
function deleteTodo(id){
  let todos = LS.getTodos();
  todos = todos.filter(t => t.id !== id);
  LS.saveTodos(todos);
  renderTodos(todos);
}

// ======= Background =======
function setRandomBackground(){
  // pick random query
  const q = BG_QUERY[Math.floor(Math.random() * BG_QUERY.length)];
  const url = `https://source.unsplash.com/1600x900/?${encodeURIComponent(q)}`;
  // set as background image -- we set an overlay for readability
  document.documentElement.style.backgroundImage = `url(${url})`;
  document.documentElement.style.backgroundSize = 'cover';
  document.documentElement.style.backgroundPosition = 'center';
  document.documentElement.style.backgroundRepeat = 'no-repeat';
  // subtle overlay
  document.documentElement.style.backgroundColor = 'rgba(0,0,0,0.28)';
  document.documentElement.style.backgroundBlendMode = 'overlay';
}

// ======= Geolocation & Weather =======
async function detectLocationAndWeather(){
  const info = window._app.weatherInfo;
  info.textContent = 'Detecting location... (you may be asked to allow location access)';
  if(!navigator.geolocation){ info.textContent = 'Geolocation not supported in this browser.'; return; }

  navigator.geolocation.getCurrentPosition(async (pos) => {
    const { latitude: lat, longitude: lon } = pos.coords;
    info.textContent = `Location detected: ${lat.toFixed(4)}, ${lon.toFixed(4)} — fetching weather...`;
    if(!API_KEY || API_KEY.includes('YOUR_')){
      info.textContent = `Location: ${lat.toFixed(4)}, ${lon.toFixed(4)}. Add an OpenWeatherMap API key in app.js to fetch weather.`;
      return;
    }
    try{
      const url = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=metric&appid=${API_KEY}`;
      const res = await fetch(url);
      if(!res.ok) throw new Error('Weather fetch failed');
      const data = await res.json();
      renderWeather(data);
    }catch(e){
      console.error(e);
      info.textContent = 'Failed to fetch weather. See console for details.';
    }
  }, (err) => {
    info.textContent = 'Location permission denied or unavailable.';
  }, { enableHighAccuracy: false, timeout: 10000 });
}

function renderWeather(data){
  const info = window._app.weatherInfo;
  if(!data || !data.main) { info.textContent = 'No weather data'; return; }
  const name = data.name || '';
  const temp = Math.round(data.main.temp);
  const desc = (data.weather && data.weather[0] && data.weather[0].description) || '';
  const hum = data.main.humidity;
  info.innerHTML = `
    <div style="font-weight:700; font-size:16px">${name} — ${temp}°C</div>
    <div style="margin-top:6px; opacity:0.95">${desc} · Humidity ${hum}%</div>
  `;
}

// ======= Init =======
function init(){
  buildUI();
  startClock();
  renderLogin();
  loadTodos();
  setRandomBackground();

  // convenience: press Enter in todo input to add
  window._app.todoInput.addEventListener('keydown', (e)=>{ if(e.key === 'Enter'){ e.preventDefault(); addTodoInput(); } });
}

// auto-run when script loads
init();

// expose some helpers for debugging in console
window._APP_DEBUG = { LS, setRandomBackground, detectLocationAndWeather };
