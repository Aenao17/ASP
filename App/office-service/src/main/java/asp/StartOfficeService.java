package asp;

import asp.model.StorageUnit;
import asp.repository.StorageUnitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StartOfficeService {
    public static void main(String[] args) {
        SpringApplication.run(StartOfficeService.class, args);
    }

    @Bean
    public CommandLineRunner initRoot(StorageUnitRepository suRepo) {
        return args -> {
            suRepo.findByName("SEDIU")
                    .orElseGet(() -> {
                        StorageUnit root = new StorageUnit();
                        root.setName("SEDIU");
                        return suRepo.save(root);
                    });
        };
    }
}