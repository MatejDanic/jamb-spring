package matej.components;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import matej.api.repositories.BoxTypeRepository;
import matej.api.repositories.ColumnTypeRepository;
import matej.api.repositories.RoleRepository;
import matej.models.AuthRole;
import matej.models.types.BoxType;
import matej.models.types.ColumnType;

@Component
public class Startup implements ApplicationRunner {

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    ColumnTypeRepository columnTypeRepo;

    @Autowired
    BoxTypeRepository boxTypeRepo;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (roleRepo.findAll().size() == 0) {
            roleRepo.saveAll(Arrays.asList(new AuthRole("ADMIN"), new AuthRole("USER")));
        }

        if (columnTypeRepo.findAll().size() == 0) {
            columnTypeRepo.saveAll(Arrays.asList(new ColumnType("DOWNWARDS"), new ColumnType("UPWARDS"),
                    new ColumnType("ANY_DIRECTION"), new ColumnType("ANNOUNCEMENT")));
        }

        if (boxTypeRepo.findAll().size() == 0) {
            boxTypeRepo.saveAll(Arrays.asList(new BoxType("ONES"), new BoxType("TWOS"), new BoxType("THREES"),
                    new BoxType("FOURS"), new BoxType("FIVES"), new BoxType("SIXES"), new BoxType("MAX"),
                    new BoxType("MIN"), new BoxType("TRIPS"), new BoxType("STRAIGHT"), new BoxType("FULL"),
                    new BoxType("POKER"), new BoxType("JAMB")));
        }
    }
}
