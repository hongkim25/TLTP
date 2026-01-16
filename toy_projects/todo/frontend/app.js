const API_URL = 'http://localhost:8081/api/todos';

//Bring what to do when the document is loaded
document.addEventListener('DOMContentLoaded', () => {
    loadTodos();
});

//Bring what to do
async function loadTodos() {
    try {
        const response = await fetch(API_URL);
        const todos = await response.json();
        renderTodos(todos);
    } catch (error) {
        console.error('Error loading todos:', error);
    }
}

//To-do list rendering
function renderTodos(todos) {
    const todoList = document.getElementById('todoList');
    
    if (todos.length === 0) {
        todoList.innerHTML = '<div class="empty-state">NOTHING TO DO YET!</div>';
        return;
    }
    
    todoList.innerHTML = todos.map(todo => `
        <li class="todo-item ${todo.completed ? 'completed' : ''}">
            <input 
                type="checkbox" 
                class="todo-checkbox" 
                ${todo.completed ? 'checked' : ''}
                onchange="toggleTodo(${todo.id}, ${!todo.completed})"
            >
            <span class="todo-text">${todo.title}</span>
            <button class="delete-btn" onclick="deleteTodo(${todo.id})">DELETE</button>
        </li>
    `).join('');
}

//Add a new to-do item
async function addTodo() {
    const input = document.getElementById('todoInput');
    const title = input.value.trim();
    
    if (!title) {
        alert('Add what to do!');
        return;
    }
    
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ title, completed: false })
        });
        
        if (response.ok) {
            input.value = '';
            loadTodos();
        }
    } catch (error) {
        console.error('Error adding todo:', error);
    }
}

// Toggle to-do completion status
async function toggleTodo(id, completed) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        const todo = await response.json();
        
        await fetch(`${API_URL}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                title: todo.title,
                completed: completed
            })
        });
        
        loadTodos();
    } catch (error) {
        console.error('Error toggling todo:', error);
    }
}

// Delete a to-do item
async function deleteTodo(id) {
    if (!confirm('Do you really want to delete this?')) {
        return;
    }
    
    try {
        await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });
        
        loadTodos();
    } catch (error) {
        console.error('Error deleting todo:', error);
    }
}