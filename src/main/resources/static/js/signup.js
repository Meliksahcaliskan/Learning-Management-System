document.addEventListener('DOMContentLoaded', function () {
  const signupForm = document.querySelector('section');
  signupForm.style.opacity = 0;

  setTimeout(() => {
    signupForm.style.transition = 'opacity 1s ease-in-out';
    signupForm.style.opacity = 1;
  }, 500);

  const signupButton = document.querySelector('button');
  signupButton.addEventListener('click', function (event) {
    event.preventDefault();  // Prevent default form submission
    
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('passwordcon');
    const roleInput = document.getElementById('type');  // User type (role_id)

    // Check for a valid email, password, and matching passwords
    const isValid = emailInput.checkValidity() && 
                    passwordInput.checkValidity() && 
                    confirmPasswordInput.checkValidity() &&
                    roleInput.checkValidity();

    if (!isValid) {
      signupForm.classList.add('shake');

      setTimeout(() => {
        signupForm.classList.remove('shake');
      }, 1000);
    } else {
      // Gather form values
      const username = usernameInput.value;
      const email = emailInput.value;
      const password = passwordInput.value;
      const confirmPassword = confirmPasswordInput.value;
      const role = roleInput.value;  // Get selected role

      // Only proceed if passwords match
      if (password === confirmPassword) {
        const data = {
          username: username,
          email: email,
          password: password,
          role_id: role  // Include role in the data payload
        };

        const jsonData = JSON.stringify(data);

        // Make the POST request to the server
        fetch('/req/signup', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: jsonData
        })
        .then(response => {
          if (response.ok) {
            alert('Signup successful');
          } else {
            alert('Signup failed');
          }
        })
        .catch(error => {
          console.error('Error:', error);
          alert('Error during signup');
        });
      } else {
        alert('Passwords do not match');
      }
    }
  });
});