let cart = {};

// --- 1. INITIALIZATION ---
document.addEventListener('DOMContentLoaded', () => {
    checkShopStatus(); // First, check if we are open
});

// --- 2. GOD SWITCH LOGIC ---
async function checkShopStatus() {
    try {
        const response = await fetch('/api/staff/status');
        const data = await response.json();

        if (data.open === true) {
            // IF OPEN: Load the menu
            console.log("더베이커 영업 중입니다.");
            fetchProducts();
        } else {
            // IF CLOSED: Kill the menu, show the banner
            console.log("지금은 영업 중이 아닙니다.");
            showClosedState();
        }
    } catch (error) {
        console.error("System offline", error);
        showClosedState();
    }
}

function showClosedState() {
    document.getElementById('productList').innerHTML = ''; // Clear products

    // Inject the "Sold Out" Banner
    const container = document.querySelector('.main-container');
    // Save the header/cart, just replace the main content area or inject into product list
    document.getElementById('productList').innerHTML = `
        <div class="sold-out-hero" style="grid-column: 1 / -1; text-align: center; padding: 50px; background: #eee; border-radius: 20px;">
            <h1 style="font-size: 3rem; color: #d63031;">⛔ SOLD OUT</h1>
            <p style="font-size: 1.5rem; margin-top: 20px;">솔드아웃 되었습니다.</p>
            <p>내일 9시에 뵙겠습니다!</p>
        </div>
    `;
}

// --- 3. PRODUCT LOGIC ---
async function fetchProducts() {
    try {
        const response = await fetch('/api/products');
        const products = await response.json();
        renderMenu(products);
    } catch (error) {
        console.error('Error fetching menu:', error);
    }
}

function renderMenu(products) {
    const container = document.getElementById('productList');
    container.innerHTML = products.map(p => `
        <div class="product-card">
            <span class="product-tag">${p.category || 'Bakery'}</span>
            <h3 class="product-name">${p.name}</h3>
            <p class="product-desc">${p.description || 'Freshly baked goodness.'}</p>
            <div class="product-footer">
                <span class="price">${p.price.toLocaleString()} ₩</span>
                <button class="add-btn" onclick="addToCart(${p.id}, '${p.name}', ${p.price})">
                    + Add
                </button>
            </div>
            <div style="margin-top:10px; font-size:0.8em; color:${p.stockQuantity < 5 ? 'red' : 'green'}">
                Stock: ${p.stockQuantity} left
            </div>
        </div>
    `).join('');
}

// --- 4. CART LOGIC ---
function addToCart(id, name, price) {
    if (!cart[id]) {
        cart[id] = { name, price, qty: 0 };
    }
    cart[id].qty++;
    renderCart();
}

function renderCart() {
    const container = document.getElementById('cartItems');
    const totalEl = document.getElementById('totalPrice');

    if (Object.keys(cart).length === 0) {
        container.innerHTML = '<p class="empty-cart">트레이가 비어 있습니다.</p>';
        totalEl.innerText = '0 ₩';
        return;
    }

    let total = 0;
    container.innerHTML = Object.entries(cart).map(([id, item]) => {
        total += item.price * item.qty;
        return `
            <div class="cart-item">
                <div>
                    <strong>${item.name}</strong>
                    <div>x ${item.qty}</div>
                </div>
                <div>${(item.price * item.qty).toLocaleString()} ₩</div>
            </div>
        `;
    }).join('');

    totalEl.innerText = total.toLocaleString() + ' ₩';
}

// --- 5. CHECKOUT LOGIC ---
async function placeOrder() {
    const phone = document.getElementById('phoneNumber').value;
    if (!phone) {
        alert("포인트 적립을 위해 핸드폰 번호를 입력해주세요.");
        return;
    }

    const itemsList = Object.entries(cart).map(([id, item]) => ({
        productId: parseInt(id),
        quantity: item.qty
    }));

    const orderRequest = {
        phoneNumber: phone,
        items: itemsList
    };

    try {
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderRequest)
        });

        if (response.ok) {
            // Calculate total for the alert
            let total = 0;
            Object.values(cart).forEach(item => total += item.price * item.qty);

            alert("✅ 예약이 완료되었습니다!\n\n " + total.toLocaleString() + " 원을 입금해주세요. \nKB 은행 123-456-7890 (김현균)\n\n입금 확인 후 준비해드리겠습니다.");
            cart = {};
            renderCart();
            fetchProducts();
        } else {
            alert("주문에 실패했습니다. 재고를 확인해주세요.");
        }
    } catch (error) {
        console.error("Order error:", error);
    }
}

// --- 6. MEMBERSHIP BARCODE LOGIC ---
function toggleMembership() {
    const modal = document.getElementById('membershipModal');
    // Toggle between showing/hiding (Flex vs None)
    if (modal.style.display === 'none') {
        modal.style.display = 'flex';
        // Reset state
        document.getElementById('loginPhase').style.display = 'block';
        document.getElementById('cardPhase').style.display = 'none';
    } else {
        modal.style.display = 'none';
    }
}

function showBarcode() {
    const phone = document.getElementById('myPhone').value;
    if (phone.length < 10) {
        alert("핸드폰 번호를 확인해주세요.");
        return;
    }

    // 1. Switch Views
    document.getElementById('loginPhase').style.display = 'none';
    document.getElementById('cardPhase').style.display = 'block';

    // 2. Generate Barcode (The Magic)
    JsBarcode("#barcode", phone, {
        format: "CODE128",
        lineColor: "#2d3436",
        width: 2,
        height: 80,
        displayValue: false
    });

    document.getElementById('displayPhone').innerText = phone;
}