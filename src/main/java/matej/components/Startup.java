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
            columnTypeRepo.saveAll(Arrays.asList(new ColumnType(1, "DOWNWARDS"), new ColumnType(2, "UPWARDS"),
                    new ColumnType(3, "ANY_DIRECTION"), new ColumnType(4, "ANNOUNCEMENT")));
        }

        if (boxTypeRepo.findAll().size() == 0) {
            boxTypeRepo.saveAll(Arrays.asList(new BoxType(1, "ONES"), new BoxType(2, "TWOS"), new BoxType(3, "THREES"),
                    new BoxType(4, "FOURS"), new BoxType(5, "FIVES"), new BoxType(6, "SIXES"), new BoxType(7, "MAX"),
                    new BoxType(8, "MIN"), new BoxType(9, "TRIPS"), new BoxType(10, "STRAIGHT"), new BoxType(11, "FULL"),
                    new BoxType(12, "POKER"), new BoxType(13, "JAMB")));
        }
    }
}
