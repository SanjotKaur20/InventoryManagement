package com.sanjot.inventory.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sanjot.inventory.entity.Department;
import com.sanjot.inventory.entity.User;
import com.sanjot.inventory.repository.DepartmentRepository;
import com.sanjot.inventory.repository.UserRepository;
import com.sanjot.inventory.services.DepartmentService;
import com.sanjot.inventory.services.EmailService;
import com.sanjot.inventory.services.OtpService;
import com.sanjot.inventory.services.UserService;

import java.util.Optional;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DepartmentService departmentService;
	@Autowired
	private OtpService otpService;

	@Autowired
	private EmailService emailService;



    public UserController(UserRepository userRepository,
    		BCryptPasswordEncoder passwordEncoder,UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService=userService;
    }

    @GetMapping("/SignUp")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("departments", departmentRepository.findAll());

        return "SignUp";
    }
//
//    @PostMapping("/signup")
//    public String processSignup(@ModelAttribute User user,
//                                Model model,
//                                @RequestParam("departmentId") Long departmentId) {
//        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
//
//        if (existingUser.isPresent()) {
//            model.addAttribute("errorMessage", "Email already exists! Please use a different email.");
//            return "SignUp";
//        }
//
//        String password = user.getPassword();
//        if (!containsSpecialCharacter(password) || !containsNumber(password) || !containsUpperCase(password)) {
//            model.addAttribute("errorMessage", "Password must contain an uppercase letter, a number, and a special character.");
//            return "SignUp";
//        }
//
//        Department dept = departmentRepository.findById(departmentId).orElse(null);
//        user.setDepartment(dept);
//
//
//        // 🔑 Generate and Send OTP
//        String otp = otpService.generateOtp(user.getEmail()); // generates and stores in memory (or DB if you're storing)
//        emailService.sendOtpEmail(user.getEmail(), otp);
//
//        // 📨 Send data to the verify-otp view
//        model.addAttribute("message", "An OTP has been sent to your email address.");
//        model.addAttribute("email", user.getEmail());
//        model.addAttribute("user", user);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setRole(User.Role.STAFF);
//        userRepository.save(user);
//
//
//        System.out.println("User registered successfully: " + user.getEmail());
//
//        return "verify-otp";  // Show OTP verification page
//    }
//
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user,
                                 Model model,
                                 @RequestParam("departmentId") Long departmentId) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            model.addAttribute("errorMessage", "Email already exists! Please use a different email.");
            return "SignUp";
        }

        String password = user.getPassword();
        if (!containsSpecialCharacter(password) || !containsNumber(password) || !containsUpperCase(password)) {
            model.addAttribute("errorMessage", "Password must contain an uppercase letter, a number, and a special character.");
            return "SignUp";
        }

        Department dept = departmentRepository.findById(departmentId).orElse(null);
        user.setDepartment(dept);
        user.setRole(User.Role.STAFF);
        user.setPassword(passwordEncoder.encode(password));  // encode the password now

        // 🔑 Generate and send OTP
        String otp = otpService.generateOtp(user.getEmail());
        emailService.sendOtpEmail(user.getEmail(), otp);

        // 📨 Pass the user details temporarily to the OTP page
        model.addAttribute("message", "An OTP has been sent to your email address.");
        model.addAttribute("email", user.getEmail());
        model.addAttribute("user", user);  // Pass the user object to verify-otp page

        // ❌ Don't save the user yet!

        return "verify-otp"; 
    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            @ModelAttribute User user,
                            Model model) {
        boolean isValid = otpService.validateOtp(email, otp);

        if (isValid) {
            userRepository.save(user);  // ✅ Save only after successful OTP verification
            model.addAttribute("successMessage", "Registration successful! You can now log in.");
            return "login";  // or redirect to login page
        } else {
            model.addAttribute("errorMessage", "Invalid OTP. Please try again.");
            model.addAttribute("email", email);
            model.addAttribute("user", user);  // Keep user data for retry
            return "verify-otp";
        }
    }


    @GetMapping("/LoginPage")
    public String showLoginPage() {
        return "LoginPage";
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard";
    }

    // Helper methods for password validation
    private boolean containsSpecialCharacter(String str) {
        return str.matches(".*[@#$&*%].*");
    }

    private boolean containsNumber(String str) {
        return str.matches(".*\\d.*");
    }

    private boolean containsUpperCase(String str) {
        return str.matches(".*[A-Z].*");
    }

    // Debugging endpoint to check password encoding issues
    @PostMapping("/debugLogin")
    public String debugLogin(@RequestParam String email, @RequestParam String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("Stored Hashed Password: " + user.getPassword());
            System.out.println("Entered Raw Password: " + password);
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("Password Match: " + matches);
            if (matches) {
                return "redirect:/dashboard";
            }
        }
        return "redirect:/LoginPage?error";
    }
    @GetMapping("/user-list")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllActiveUsers());
        return "user_list"; // Not in a 'users/' folder
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STAFF')")
    public String showOwnProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return "redirect:/edit-user/" + user.getId();
        }
        return "redirect:/LoginPage?error";
    }

    @PostMapping("/save-user")
    public String saveUser(@ModelAttribute User user,@RequestParam("departmentId") Long departmentId) {
        Department department = departmentRepository.findById(departmentId).orElse(null);
        user.setDepartment(department);
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        user.setRole(existingUser.getRole()); // Enforce old role


        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // encode and save password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else if (user.getId() != null) {
            // editing existing user but password not entered, preserve old one
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
            }
        }

        userService.saveUser(user);
        return "redirect:/user-list";
    }

    @GetMapping("/edit-user/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "user_form"; // Same form for both add and edit
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return "redirect:/user-list";
    }
    @GetMapping("/add-user")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("departments", departmentRepository.findAll());
        return "add-user";  // No /admin/, since the file is directly inside /templates/
    }

    @PostMapping("/add-user")
    public String addUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        userService.addUser(user);
        redirectAttributes.addFlashAttribute("success", "User added successfully!");
        return "redirect:/add-user";
    }


}
