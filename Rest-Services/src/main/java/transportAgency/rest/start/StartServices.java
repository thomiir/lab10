package transportAgency.rest.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@ComponentScan({"transportAgency.persistence", "transportAgency.rest.services"})
@SpringBootApplication
public class StartServices {
    public static void main(String[] args) {
        SpringApplication.run(StartServices.class, args);
    }

    @Bean(name="properties")
    @Primary
    public Properties getBdProperties(){
        Properties props = new Properties();
        try {
            System.out.println("Searching bd.config in directory "+((new File(".")).getAbsolutePath()));
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.err.println("Configuration file bd.cong not found" + e);
        }
        return props;
    }
}

