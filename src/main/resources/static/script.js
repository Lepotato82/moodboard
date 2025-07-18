// MoodBoard JavaScript
const quotes = [
    {
        text: "The only way to do great work is to love what you do.",
        author: "Steve Jobs"
    },
    {
        text: "Innovation distinguishes between a leader and a follower.",
        author: "Steve Jobs"
    },
    {
        text: "Life is what happens to you while you're busy making other plans.",
        author: "John Lennon"
    },
    {
        text: "The future belongs to those who believe in the beauty of their dreams.",
        author: "Eleanor Roosevelt"
    },
    {
        text: "It is during our darkest moments that we must focus to see the light.",
        author: "Aristotle"
    }
];

let quoteCount = 0;
let currentQuoteIndex = -1;

// DOM elements
const quoteText = document.getElementById('quoteText');
const quoteAuthor = document.getElementById('quoteAuthor');
const generateBtn = document.getElementById('generateQuote');
const quoteCountEl = document.getElementById('quoteCount');
const themeToggle = document.getElementById('themeToggle');

// Generate random quote
function generateRandomQuote() {
    let randomIndex;
    
    // Ensure we don't get the same quote twice in a row
    do {
        randomIndex = Math.floor(Math.random() * quotes.length);
    } while (randomIndex === currentQuoteIndex && quotes.length > 1);
    
    currentQuoteIndex = randomIndex;
    const quote = quotes[randomIndex];
    
    // Add fade out effect
    quoteText.style.opacity = '0';
    quoteAuthor.style.opacity = '0';
    
    setTimeout(() => {
        quoteText.textContent = quote.text;
        quoteAuthor.textContent = `— ${quote.author}`;
        
        // Fade in effect
        quoteText.style.opacity = '1';
        quoteAuthor.style.opacity = '1';
        
        // Update counter
        quoteCount++;
        quoteCountEl.textContent = `Quotes generated: ${quoteCount}`;
    }, 200);
    
    // Add button animation
    generateBtn.style.transform = 'scale(0.95)';
    setTimeout(() => {
        generateBtn.style.transform = 'scale(1)';
    }, 150);
}

// Theme toggle functionality
function toggleTheme() {
    const body = document.body;
    const isDark = body.classList.contains('dark-theme');
    
    if (isDark) {
        body.classList.remove('dark-theme');
        themeToggle.textContent = '🌙';
    } else {
        body.classList.add('dark-theme');
        themeToggle.textContent = '☀️';
    }
}

// Event listeners
generateBtn.addEventListener('click', generateRandomQuote);
themeToggle.addEventListener('click', toggleTheme);

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
    if (e.code === 'Space' || e.key === 'Enter') {
        e.preventDefault();
        generateRandomQuote();
    }
    if (e.key === 't' || e.key === 'T') {
        toggleTheme();
    }
});

// Initialize with system theme preference
if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    document.body.classList.add('dark-theme');
    themeToggle.textContent = '☀️';
}

// Welcome message
console.log('🌟 Welcome to MoodBoard! Press Space/Enter to generate quotes, T to toggle theme.');