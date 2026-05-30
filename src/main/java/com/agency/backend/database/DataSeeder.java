package com.agency.backend.database;

import com.agency.backend.modules.projects.entity.Project;
import com.agency.backend.modules.projects.repository.ProjectRepository;
import com.agency.backend.modules.services.entity.ServiceEntity;
import com.agency.backend.modules.services.repository.ServiceRepository;
import com.agency.backend.modules.users.entity.Role;
import com.agency.backend.modules.users.entity.User;
import com.agency.backend.modules.users.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, ServiceRepository serviceRepository,
                      ProjectRepository projectRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedServices();
        seedProjects();
    }

    private void seedAdmin() {
        if (!userRepository.existsByEmail("admin@agency.com")) {
            User admin = User.builder()
                    .email("admin@agency.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("NexCore Admin")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created: admin@agency.com / Admin@123");
        }
    }

    private void seedServices() {
        if (serviceRepository.existsByIsActiveTrue()) return;

        List<ServiceEntity> services = List.of(
                ServiceEntity.builder().title("Full-Stack Development").displayOrder(1)
                        .description("End-to-end web applications from pixel-perfect frontends to robust backends, REST APIs, authentication, and cloud deployment.")
                        .icon("Code")
                        .features(List.of("React / Next.js / Vue frontends", "Spring Boot / Node.js backends", "REST & GraphQL APIs", "JWT authentication", "CI/CD pipelines", "Cloud deployment (AWS / GCP / Azure)"))
                        .build(),
                ServiceEntity.builder().title("SaaS Platform Engineering").displayOrder(2)
                        .description("Design and build multi-tenant SaaS platforms with subscription billing, usage analytics, and enterprise-grade security.")
                        .icon("Cloud")
                        .features(List.of("Multi-tenant architecture", "Stripe subscription billing", "Usage analytics dashboards", "Role-based access control", "White-label support", "Auto-scaling infrastructure"))
                        .build(),
                ServiceEntity.builder().title("E-Commerce Solutions").displayOrder(3)
                        .description("High-converting online stores with custom checkout flows, inventory management, and payment gateway integration.")
                        .icon("ShoppingCart")
                        .features(List.of("Custom storefront design", "Headless commerce architecture", "Payment gateway integration", "Inventory management", "Order tracking", "Mobile-first shopping experience"))
                        .build(),
                ServiceEntity.builder().title("API Design & Integration").displayOrder(4)
                        .description("Design, build, and document scalable REST and GraphQL APIs that connect your systems and power third-party integrations.")
                        .icon("Zap")
                        .features(List.of("OpenAPI / Swagger documentation", "RESTful & GraphQL APIs", "Third-party integrations", "Webhook systems", "API rate limiting & security", "SDK generation"))
                        .build(),
                ServiceEntity.builder().title("Cloud Infrastructure & DevOps").displayOrder(5)
                        .description("Automate your infrastructure with modern DevOps practices — from containerisation to monitoring and zero-downtime deployments.")
                        .icon("Server")
                        .features(List.of("Docker & Kubernetes", "GitHub Actions / GitLab CI", "Infrastructure as Code (Terraform)", "Monitoring & alerting", "Zero-downtime deployments", "Cost optimisation"))
                        .build(),
                ServiceEntity.builder().title("Database Architecture").displayOrder(6)
                        .description("Design scalable, performant database schemas for PostgreSQL, MySQL, MongoDB, and Redis with migration strategies and query optimisation.")
                        .icon("Database")
                        .features(List.of("Schema design & normalisation", "Query optimisation", "Read replicas & sharding", "Automated migrations", "Backup & disaster recovery", "Redis caching layers"))
                        .build(),
                ServiceEntity.builder().title("Technical Consulting").displayOrder(7)
                        .description("Architecture reviews, tech stack recommendations, code audits, and hands-on mentoring for engineering teams at any stage.")
                        .icon("Lightbulb")
                        .features(List.of("Architecture design reviews", "Tech stack selection", "Code quality audits", "Security assessments", "Performance profiling", "Team training & mentoring"))
                        .build()
        );
        serviceRepository.saveAll(services);
        log.info("Seeded {} services", services.size());
    }

    private void seedProjects() {
        if (projectRepository.count() > 0) return;

        List<Project> projects = List.of(
                Project.builder().title("FinTrack SaaS Dashboard").category("SaaS").displayOrder(1).isFeatured(true)
                        .description("Multi-tenant financial analytics platform with real-time charts, budget tracking, and team collaboration features for SMEs.")
                        .technologies(List.of("Next.js", "Spring Boot", "PostgreSQL", "Redis", "Stripe", "AWS"))
                        .imageUrl("https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800")
                        .liveUrl("https://fintrack.example.com")
                        .build(),
                Project.builder().title("ShopForge E-Commerce").category("E-Commerce").displayOrder(2).isFeatured(true)
                        .description("Headless e-commerce platform with custom checkout, AI-powered recommendations, and real-time inventory management for fashion brands.")
                        .technologies(List.of("React", "Node.js", "MongoDB", "Stripe", "Algolia", "Vercel"))
                        .imageUrl("https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=800")
                        .liveUrl("https://shopforge.example.com")
                        .build(),
                Project.builder().title("OpsCenter Admin Portal").category("Dashboard").displayOrder(3).isFeatured(true)
                        .description("Internal operations dashboard with role-based access, workflow automation, and real-time KPI monitoring for a logistics company.")
                        .technologies(List.of("Vue 3", "Spring Boot", "PostgreSQL", "WebSocket", "Docker", "GCP"))
                        .imageUrl("https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800")
                        .build(),
                Project.builder().title("DevConnect Community Platform").category("SaaS").displayOrder(4).isFeatured(false)
                        .description("Developer community platform with forums, code sharing, job board, and mentorship matching — 12,000+ active users.")
                        .technologies(List.of("Next.js", "FastAPI", "PostgreSQL", "Redis", "Elasticsearch", "AWS"))
                        .imageUrl("https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800")
                        .liveUrl("https://devconnect.example.com")
                        .build(),
                Project.builder().title("NexStore Marketplace").category("E-Commerce").displayOrder(5).isFeatured(false)
                        .description("Multi-vendor marketplace with seller onboarding, escrow payments, dispute resolution, and mobile apps for iOS and Android.")
                        .technologies(List.of("React Native", "Express", "PostgreSQL", "Stripe Connect", "Firebase", "GCP"))
                        .imageUrl("https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=800")
                        .liveUrl("https://nexstore.example.com")
                        .build()
        );
        projectRepository.saveAll(projects);
        log.info("Seeded {} projects", projects.size());
    }
}
