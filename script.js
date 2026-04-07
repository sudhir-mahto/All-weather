// ====================== CONFIGURATION ======================
const API_BASE_URL = 'http://localhost:8080/api';

// ====================== MOBILE HAMBURGER MENU ======================
const hamburger = document.getElementById('hamburger');
const navLinks  = document.getElementById('navLinks');

hamburger.addEventListener('click', () => {
    navLinks.classList.toggle('active');

    if (navLinks.classList.contains('active')) {
        hamburger.style.transform = 'rotate(90deg)';
        hamburger.textContent = '\u2715';
    } else {
        hamburger.style.transform = 'rotate(0deg)';
        hamburger.textContent = '\u2630';
    }
});

// Smooth Scrolling + Close Mobile Menu when link is clicked
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        if (this.getAttribute('href') !== '#') {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({ behavior: 'smooth' });
                navLinks.classList.remove('active');
                hamburger.style.transform = 'rotate(0deg)';
                hamburger.textContent = '\u2630';
            }
        }
    });
});

// ====================== LIVE LOCATION SHARE FEATURE ======================
const shareLocationBtn = document.getElementById('shareLocationBtn');

if (shareLocationBtn) {
    shareLocationBtn.addEventListener('click', () => {
        const originalHTML = shareLocationBtn.innerHTML;
        shareLocationBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Getting Location...';
        shareLocationBtn.disabled = true;

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const lat = position.coords.latitude;
                    const lon = position.coords.longitude;
                    const googleMapsLink = `https://www.google.com/maps?q=${lat},${lon}`;
                    const message = `Hello All Weather Solution,%0A%0AMujhe AC Service chahiye. Meri current location:%0A${googleMapsLink}%0A%0APlease jaldi aayein. Dhanyavaad!`;
                    const whatsappURL = `https://wa.me/919661332544?text=${message}`;
                    window.open(whatsappURL, '_blank');
                    shareLocationBtn.innerHTML = originalHTML;
                    shareLocationBtn.disabled = false;
                },
                (error) => {
                    let errorMsg = 'Location nahi mil pa rahi hai.';
                    switch (error.code) {
                        case error.PERMISSION_DENIED:
                            errorMsg = 'Please allow location permission to share your live location.';
                            break;
                        case error.POSITION_UNAVAILABLE:
                            errorMsg = 'Location information unavailable.';
                            break;
                        case error.TIMEOUT:
                            errorMsg = 'Location request timed out.';
                            break;
                    }
                    alert(errorMsg);
                    shareLocationBtn.innerHTML = originalHTML;
                    shareLocationBtn.disabled = false;
                }
            );
        } else {
            alert('Aapka browser live location support nahi karta hai.');
            shareLocationBtn.innerHTML = originalHTML;
            shareLocationBtn.disabled = false;
        }
    });
}

// ====================== MODAL HELPERS ======================
function openModal(overlayId) {
    const overlay = document.getElementById(overlayId);
    if (overlay) {
        overlay.classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(overlayId) {
    const overlay = document.getElementById(overlayId);
    if (overlay) {
        overlay.classList.remove('active');
        document.body.style.overflow = '';
    }
}

// Close modal on overlay background click
document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
            overlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    });
});

// Close on Escape key
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.active').forEach(overlay => {
            overlay.classList.remove('active');
            document.body.style.overflow = '';
        });
    }
});

// ====================== BOOKING MODAL ======================
const heroBookBtn   = document.getElementById('heroBookBtn');
const navBookBtn    = document.getElementById('navBookBtn');
const closeBooking  = document.getElementById('closeBookingModal');

heroBookBtn?.addEventListener('click',  () => openModal('bookingModalOverlay'));
navBookBtn?.addEventListener('click',   (e) => { e.preventDefault(); openModal('bookingModalOverlay'); });
closeBooking?.addEventListener('click', () => closeModal('bookingModalOverlay'));

// "Book Now" buttons on service cards
document.addEventListener('click', (e) => {
    if (e.target.matches('.btn-book-service')) {
        const service = e.target.getAttribute('data-service') || '';
        openModal('bookingModalOverlay');
        const serviceSelect = document.getElementById('bookingService');
        if (serviceSelect && service) {
            // Try to match an option
            for (const opt of serviceSelect.options) {
                if (opt.value === service) {
                    serviceSelect.value = service;
                    break;
                }
            }
        }
    }
});

// Set minimum date for booking (today)
const bookingDateInput = document.getElementById('bookingDate');
if (bookingDateInput) {
    const today = new Date().toISOString().split('T')[0];
    bookingDateInput.min = today;
}

// ====================== BOOKING FORM SUBMISSION ======================
const bookingForm = document.getElementById('bookingForm');

bookingForm?.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Clear previous errors
    clearFormErrors('bookingForm');

    const data = {
        customerName:  document.getElementById('bookingName').value.trim(),
        phone:         document.getElementById('bookingPhone').value.trim(),
        email:         document.getElementById('bookingEmail').value.trim() || null,
        address:       document.getElementById('bookingAddress').value.trim(),
        serviceType:   document.getElementById('bookingService').value,
        preferredDate: document.getElementById('bookingDate').value || null,
        notes:         document.getElementById('bookingNotes').value.trim() || null,
    };

    // Client-side validation
    let hasError = false;
    if (!data.customerName || data.customerName.length < 2) {
        showFieldError('bookingNameError', 'bookingName', 'Please enter your full name (min 2 characters)');
        hasError = true;
    }
    if (!data.phone || !/^[6-9]\d{9}$/.test(data.phone)) {
        showFieldError('bookingPhoneError', 'bookingPhone', 'Enter a valid 10-digit Indian mobile number');
        hasError = true;
    }
    if (!data.address) {
        showFieldError('bookingAddressError', 'bookingAddress', 'Please enter your address');
        hasError = true;
    }
    if (!data.serviceType) {
        showFieldError('bookingServiceError', 'bookingService', 'Please select a service');
        hasError = true;
    }
    if (hasError) return;

    const submitBtn = document.getElementById('bookingSubmitBtn');
    setButtonLoading(submitBtn, true, 'Booking...');

    try {
        const response = await fetch(`${API_BASE_URL}/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        const result = await response.json();

        if (response.ok && result.success) {
            showFeedback('bookingFeedback', 'success',
                `\u2714 ${result.message} (Booking ID: #${result.bookingId})`);
            bookingForm.reset();
            // Auto-close after 3 seconds
            setTimeout(() => {
                closeModal('bookingModalOverlay');
                hideFeedback('bookingFeedback');
            }, 3500);
        } else {
            // Validation errors from server
            const errorMsg = extractServerError(result);
            showFeedback('bookingFeedback', 'error', '\u26A0 ' + errorMsg);
        }
    } catch (err) {
        // Network/API not running fallback
        console.warn('Booking API unavailable, falling back to WhatsApp:', err);
        const waMsg = buildWhatsAppMessage(data);
        window.open(`https://wa.me/919661332544?text=${waMsg}`, '_blank');
        showFeedback('bookingFeedback', 'success',
            '\u2714 API not connected — opened WhatsApp instead. Please send the pre-filled message.');
    } finally {
        setButtonLoading(submitBtn, false, '\u2714 Confirm Booking');
    }
});

function buildWhatsAppMessage(data) {
    return encodeURIComponent(
        `*New AC Service Booking*\n\n` +
        `Name: ${data.customerName}\n` +
        `Phone: ${data.phone}\n` +
        `Address: ${data.address}\n` +
        `Service: ${data.serviceType}\n` +
        (data.preferredDate ? `Date: ${data.preferredDate}\n` : '') +
        (data.notes ? `Notes: ${data.notes}\n` : '')
    );
}

// ====================== CONTACT FORM SUBMISSION ======================
const contactForm = document.getElementById('contactForm');

contactForm?.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearFormErrors('contactForm');

    const data = {
        name:    document.getElementById('contactName').value.trim(),
        phone:   document.getElementById('contactPhone').value.trim(),
        email:   document.getElementById('contactEmail').value.trim() || null,
        message: document.getElementById('contactMessage').value.trim(),
    };

    // Validate
    let hasError = false;
    if (!data.name || data.name.length < 2) {
        showFieldError('contactNameError', 'contactName', 'Please enter your name (min 2 characters)');
        hasError = true;
    }
    if (!data.phone || !/^[6-9]\d{9}$/.test(data.phone)) {
        showFieldError('contactPhoneError', 'contactPhone', 'Enter a valid 10-digit Indian mobile number');
        hasError = true;
    }
    if (!data.message || data.message.length < 10) {
        showFieldError('contactMessageError', 'contactMessage', 'Please write a message (min 10 characters)');
        hasError = true;
    }
    if (hasError) return;

    const submitBtn = document.getElementById('contactSubmitBtn');
    setButtonLoading(submitBtn, true, 'Sending...');

    try {
        const response = await fetch(`${API_BASE_URL}/contacts`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        const result = await response.json();

        if (response.ok && result.success) {
            showFeedback('contactFeedback', 'success', '\u2714 ' + result.message);
            contactForm.reset();
        } else {
            showFeedback('contactFeedback', 'error', '\u26A0 ' + extractServerError(result));
        }
    } catch (err) {
        console.warn('Contact API unavailable, falling back to WhatsApp:', err);
        const waMsg = encodeURIComponent(
            `*Message from ${data.name}*\nPhone: ${data.phone}\n\n${data.message}`
        );
        window.open(`https://wa.me/919661332544?text=${waMsg}`, '_blank');
        showFeedback('contactFeedback', 'success',
            '\u2714 API not connected \u2014 opened WhatsApp instead.');
    } finally {
        setButtonLoading(submitBtn, false, '\u2708 Send Message');
    }
});

// ====================== LOAD SERVICES FROM API ======================
async function loadServices() {
    try {
        const response = await fetch(`${API_BASE_URL}/services`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' },
        });

        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const services = await response.json();
        if (!services || services.length === 0) return; // Keep static cards

        const grid = document.getElementById('servicesGrid');
        if (!grid) return;

        // Remove all static fallback cards
        grid.querySelectorAll('[data-static="true"]').forEach(el => el.remove());

        // Render API cards
        services.forEach(service => {
            const card = document.createElement('div');
            card.className = 'card';
            const priceDisplay = buildPriceDisplay(service);
            card.innerHTML = `
                <div class="card-icon"><i class="fas ${service.icon || 'fa-tools'}"></i></div>
                <h3>${escapeHtml(service.name)}</h3>
                <p>${escapeHtml(service.description)}</p>
                <div class="card-price">${priceDisplay}</div>
                <button class="btn-book-service" data-service="${escapeHtml(service.name)}">Book Now</button>
            `;
            grid.appendChild(card);
        });
    } catch (err) {
        // Silently keep static cards
        console.info('Services API not available, using static content:', err.message);
    }
}

function buildPriceDisplay(service) {
    if (service.priceUnit === 'contact' || service.price === 0) {
        return 'Call for Quote';
    }
    const symbol = '\u20B9';
    if (service.priceUnit === 'per year') {
        return `${symbol}${service.price.toLocaleString('en-IN')}/year`;
    }
    return `Starting ${symbol}${service.price.toLocaleString('en-IN')}`;
}

// ====================== LOAD REVIEWS FROM API ======================
async function loadReviews() {
    const grid = document.getElementById('reviewsGrid');
    if (!grid) return;

    try {
        const response = await fetch(`${API_BASE_URL}/reviews`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' },
        });

        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const reviews = await response.json();
        grid.innerHTML = '';

        if (!reviews || reviews.length === 0) {
            grid.innerHTML = `
                <div class="no-reviews">
                    <i class="fas fa-star" style="color:#ddd;font-size:2.5rem;display:block;margin-bottom:12px;"></i>
                    <p>No reviews yet. Be the first to write one!</p>
                </div>`;
            return;
        }

        reviews.forEach(review => {
            const stars = buildStarsHtml(review.rating);
            const card = document.createElement('div');
            card.className = 'review-card';
            card.innerHTML = `
                <div class="review-stars">${stars}</div>
                <p class="review-comment">${escapeHtml(review.comment)}</p>
                <div class="review-meta">
                    <span class="review-author">${escapeHtml(review.customerName)}</span>
                    ${review.serviceType ? `<span class="review-service-tag">${escapeHtml(review.serviceType)}</span>` : ''}
                </div>
            `;
            grid.appendChild(card);
        });
    } catch (err) {
        console.info('Reviews API not available:', err.message);
        // Show a placeholder
        grid.innerHTML = `
            <div class="no-reviews">
                <i class="fas fa-wifi" style="color:#ddd;font-size:2rem;display:block;margin-bottom:12px;"></i>
                <p>Reviews will load once the backend is running.</p>
            </div>`;
    }
}

function buildStarsHtml(rating) {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        stars += i <= rating ? '\u2605' : '\u2606';
    }
    return stars;
}

// ====================== REVIEW MODAL ======================
const openReviewBtn  = document.getElementById('openReviewBtn');
const closeReviewBtn = document.getElementById('closeReviewModal');

openReviewBtn?.addEventListener('click',  () => openModal('reviewModalOverlay'));
closeReviewBtn?.addEventListener('click', () => closeModal('reviewModalOverlay'));

// Star Rating Interaction
let selectedRating = 0;
const stars = document.querySelectorAll('.star');
const ratingInput = document.getElementById('reviewRating');

stars.forEach(star => {
    // Hover: highlight stars up to hovered one
    star.addEventListener('mouseenter', () => {
        const val = parseInt(star.getAttribute('data-value'));
        stars.forEach(s => {
            s.classList.toggle('active', parseInt(s.getAttribute('data-value')) <= val);
        });
    });

    // Click: lock the rating
    star.addEventListener('click', () => {
        selectedRating = parseInt(star.getAttribute('data-value'));
        if (ratingInput) ratingInput.value = selectedRating;
        stars.forEach(s => {
            s.classList.toggle('active', parseInt(s.getAttribute('data-value')) <= selectedRating);
        });
    });
});

// On mouse leave, revert to selected rating
const starRatingContainer = document.getElementById('starRating');
starRatingContainer?.addEventListener('mouseleave', () => {
    stars.forEach(s => {
        s.classList.toggle('active', parseInt(s.getAttribute('data-value')) <= selectedRating);
    });
});

// ====================== REVIEW FORM SUBMISSION ======================
const reviewForm = document.getElementById('reviewForm');

reviewForm?.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearFormErrors('reviewForm');

    const data = {
        customerName: document.getElementById('reviewName').value.trim(),
        rating:       parseInt(document.getElementById('reviewRating').value) || 0,
        comment:      document.getElementById('reviewComment').value.trim(),
        serviceType:  document.getElementById('reviewService').value || null,
    };

    // Validate
    let hasError = false;
    if (!data.customerName || data.customerName.length < 2) {
        showFieldError('reviewNameError', 'reviewName', 'Please enter your name');
        hasError = true;
    }
    if (!data.rating || data.rating < 1 || data.rating > 5) {
        showFieldError('reviewRatingError', '', 'Please select a star rating');
        hasError = true;
    }
    if (!data.comment || data.comment.length < 10) {
        showFieldError('reviewCommentError', 'reviewComment', 'Please write a review (min 10 characters)');
        hasError = true;
    }
    if (hasError) return;

    const submitBtn = document.getElementById('reviewSubmitBtn');
    setButtonLoading(submitBtn, true, 'Submitting...');

    try {
        const response = await fetch(`${API_BASE_URL}/reviews`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        const result = await response.json();

        if (response.ok && result.success) {
            showFeedback('reviewFeedback', 'success', '\u2714 ' + result.message);
            reviewForm.reset();
            selectedRating = 0;
            stars.forEach(s => s.classList.remove('active'));
            if (ratingInput) ratingInput.value = 0;
        } else {
            showFeedback('reviewFeedback', 'error', '\u26A0 ' + extractServerError(result));
        }
    } catch (err) {
        showFeedback('reviewFeedback', 'error',
            '\u26A0 Could not submit review right now. Please try again later.');
        console.error('Review submission error:', err);
    } finally {
        setButtonLoading(submitBtn, false, '\u2708 Submit Review');
    }
});

// ====================== UTILITY FUNCTIONS ======================

/**
 * Show a field-level error message and mark input as invalid.
 */
function showFieldError(errorElementId, inputId, message) {
    const errorEl = document.getElementById(errorElementId);
    if (errorEl) errorEl.textContent = message;
    if (inputId) {
        const inputEl = document.getElementById(inputId);
        if (inputEl) inputEl.classList.add('input-error');
    }
}

/**
 * Clear all field errors in a form.
 */
function clearFormErrors(formId) {
    const form = document.getElementById(formId);
    if (!form) return;
    form.querySelectorAll('.field-error').forEach(el => (el.textContent = ''));
    form.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
    // Hide feedback messages
    form.querySelectorAll('.form-feedback').forEach(el => {
        el.className = 'form-feedback';
        el.textContent = '';
    });
}

/**
 * Show a feedback banner (success/error).
 */
function showFeedback(elementId, type, message) {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.className = `form-feedback ${type}`;
    el.textContent = message;
}

/**
 * Hide a feedback banner.
 */
function hideFeedback(elementId) {
    const el = document.getElementById(elementId);
    if (el) {
        el.className = 'form-feedback';
        el.textContent = '';
    }
}

/**
 * Toggle button loading state.
 */
function setButtonLoading(btn, loading, label) {
    if (!btn) return;
    btn.disabled = loading;
    if (loading) {
        btn.dataset.originalHtml = btn.innerHTML;
        btn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> ${label}`;
    } else {
        btn.innerHTML = btn.dataset.originalHtml || label;
    }
}

/**
 * Extract error message from server response.
 */
function extractServerError(result) {
    if (typeof result === 'string') return result;
    return result.message || result.error || 'Something went wrong. Please try again.';
}

/**
 * Escape HTML to prevent XSS when inserting API data into the DOM.
 */
function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

// ====================== INITIALISE ON PAGE LOAD ======================
document.addEventListener('DOMContentLoaded', () => {
    loadServices();
    loadReviews();
});
