package com.domain.ems.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.domain.ems.models.electonics.Accessories;
import com.domain.ems.repository.AccessoriesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AccessoryService {

    @Autowired
    private AccessoriesRepository accessoriesRepository;
    
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    ObjectMapper om = new ObjectMapper();
    
    @Transactional
    public void writeToOutputStream(final OutputStream outputStream) {
        try (Stream<Accessories> accResultStream = accessoriesRepository.findAllAccQueryAndStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            	accResultStream.forEach(acc -> {
                    try {
                        oos.write(om.writeValueAsBytes(acc));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
