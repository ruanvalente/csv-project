package example.csv.controller;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import example.csv.entity.Customers;
import example.csv.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository repository;

    @GetMapping
    public Page<Customers> customersList(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCustomers(@RequestParam("file") MultipartFile file) {
        try {
            CSVReader csvReader =
                    new CSVReader(new InputStreamReader(file.getInputStream()));
            List<Customers> customers = new ArrayList<>();

            String[] nextLine;
            csvReader.readNext();

            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length >= 2) {
                    String name = nextLine[0];
                    String email = nextLine[1];
                    Customers entity = new Customers(name, email);
                    customers.add(entity);
                }
            }
            repository.saveAll(customers);
            csvReader.close();
            return ResponseEntity.ok("Dados gravados com sucesso!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao ler o arquivo CSV.");
        } catch (CsvValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na validação do arquivo CSV.");
        }
    }
}
