// A simple Service Worker to make the app "Installable"
self.addEventListener('install', (e) => {
  console.log('[Service Worker] Install');
});

self.addEventListener('fetch', (e) => {
  // Just pass requests through (Network First)
  e.respondWith(fetch(e.request));
});