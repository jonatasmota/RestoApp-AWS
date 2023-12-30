"use strict";

/* Navbar Toggle */

const navbar = document.querySelector("[data-navbar]");
const navbarLinks = document.querySelector("[data-nav-link]");
const menuToggleBtn = document.querySelector("[data-nav-toggle-btn]");

menuToggleBtn.addEventListener("click", function () {
  navbar.classList.toggle("active");
  this.classList.toggle("active");
});

for (let i = 0; i < navbarLinks.length; i++) {
  navbarLinks[i].addEventListener("click", function () {
    navbar.classList.toggle("active");
    menuToggleBtn.classList.toggle("active");
  });
}

/* Header Stick and back to Top */

const header = document.querySelector("[data-header]");
const backTopBtn = document.querySelector("[data-back-top-btn]");

window.addEventListener("scroll", function () {
  if (this.window.scrollY >= 100) {
    header.classList.add("active");
    backTopBtn.classList.add("active");
  } else {
    header.classList.remove("active");
    backTopBtn.classList.remove("active");
  }
});

/* Get actual date */
// document.getElementById("date-picker").value = new Date()
//   .toISOString()
//   .substring(0, 10);

/* Login Modal */
$(document).ready(function () {
  $("#loginModal").modal("show");
  $(function () {
    $('[data-toggle="tooltip"]').tooltip();
  });
});
