// ====================== MOBILE HAMBURGER MENU ======================
const hamburger = document.getElementById('hamburger');
const navLinks = document.getElementById('navLinks');

hamburger.addEventListener('click', () => {
    navLinks.classList.toggle('active');
    
    // Hamburger icon animation
    if (navLinks.classList.contains('active')) {
        hamburger.style.transform = 'rotate(90deg)';
        hamburger.textContent = '✕';
    } else {
        hamburger.style.transform = 'rotate(0deg)';
        hamburger.textContent = '☰';
    }
});

// Smooth Scrolling + Close Mobile Menu when link is clicked
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        if (this.getAttribute('href') !== '#') {
            e.preventDefault();
            
            const target = document.querySelector(this.getAttribute('href'));
            
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth'
                });
                
                // Close mobile menu after clicking
                navLinks.classList.remove('active');
                hamburger.style.transform = 'rotate(0deg)';
                hamburger.textContent = '☰';
            }
        }
    });
});

// ====================== LIVE LOCATION SHARE FEATURE ======================
const shareLocationBtn = document.getElementById('shareLocationBtn');

if (shareLocationBtn) {
    shareLocationBtn.addEventListener('click', () => {
        
        // Show loading state
        const originalText = shareLocationBtn.innerHTML;
        shareLocationBtn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Getting Location...`;
        shareLocationBtn.disabled = true;

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const lat = position.coords.latitude;
                    const lon = position.coords.longitude;
                    
                    // Google Maps link
                    const googleMapsLink = `https://www.google.com/maps?q=${lat},${lon}`;
                    
                    // WhatsApp message
                    const message = `Hello All Weather Solution,%0A%0AI need AC Service at my current location:%0A${googleMapsLink}%0A%0APlease come as soon as possible. Thank you!`;
                    
                    // Open WhatsApp
                    const whatsappURL = `https://wa.me/919661332544?text=${message}`;
                    window.open(whatsappURL, '_blank');
                    
                    // Reset button
                    shareLocationBtn.innerHTML = originalText;
                    shareLocationBtn.disabled = false;
                },
                
                (error) => {
                    let errorMsg = "Location nahi mil pa rahi hai.";
                    
                    switch(error.code) {
                        case error.PERMISSION_DENIED:
                            errorMsg = "Please allow location permission to share your live location.";
                            break;
                        case error.POSITION_UNAVAILABLE:
                            errorMsg = "Location information unavailable.";
                            break;
                        case error.TIMEOUT:
                            errorMsg = "Location request timed out.";
                            break;
                    }
                    
                    alert(errorMsg);
                    shareLocationBtn.innerHTML = originalText;
                    shareLocationBtn.disabled = false;
                }
            );
        } else {
            alert("Aapka browser live location support nahi karta hai.");
            shareLocationBtn.innerHTML = originalText;
            shareLocationBtn.disabled = false;
        }
    });
}