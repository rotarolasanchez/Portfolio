const CACHE_NAME = 'kotlin-chatbot-v1';
const urlsToCache = [
    '/',
    '/index.html',
    '/shared.js',
    '/manifest.json',
    '/icon-192x192.png',
    '/icon-512x512.png'
];

// Install Service Worker
self.addEventListener('install', (event) => {
    console.log('Service Worker installing...');
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then((cache) => {
                console.log('Cache opened');
                return cache.addAll(urlsToCache);
            })
    );
});

// Fetch event
self.addEventListener('fetch', (event) => {
    event.respondWith(
        caches.match(event.request)
            .then((response) => {
                // Return cached version or fetch from network
                if (response) {
                    return response;
                }
                return fetch(event.request).then((response) => {
                    // Don't cache non-successful responses
                    if (!response || response.status !== 200 || response.type !== 'basic') {
                        return response;
                    }

                    // Clone the response
                    const responseToCache = response.clone();

                    caches.open(CACHE_NAME)
                        .then((cache) => {
                            cache.put(event.request, responseToCache);
                        });

                    return response;
                });
            })
    );
});

// Activate event
self.addEventListener('activate', (event) => {
    console.log('Service Worker activating...');
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames.map((cacheName) => {
                    if (cacheName !== CACHE_NAME) {
                        console.log('Deleting old cache:', cacheName);
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
});

// Background sync (for future offline functionality)
self.addEventListener('sync', (event) => {
    if (event.tag === 'chat-sync') {
        console.log('Background sync for chat messages');
        // Aquí podrías sincronizar mensajes offline
    }
});

// Push notifications (for future real-time features)
self.addEventListener('push', (event) => {
    console.log('Push notification received');

    const options = {
        body: event.data ? event.data.text() : 'Nueva notificación del ChatBot',
        icon: '/icon-192x192.png',
        badge: '/icon-192x192.png',
        vibrate: [100, 50, 100],
        data: {
            url: '/'
        }
    };

    event.waitUntil(
        self.registration.showNotification('Kotlin ChatBot', options)
    );
});

// Notification click
self.addEventListener('notificationclick', (event) => {
    console.log('Notification clicked');

    event.notification.close();

    event.waitUntil(
        clients.openWindow(event.notification.data.url)
    );
});
