package ru.bukhtaev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.util.NameableSort;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public abstract class AbstractIntegrationTest extends AbstractContainerizedTest {

    protected static final LinkedMultiValueMap<String, String> NAMEABLE_PAGE_REQUEST_PARAMS = new LinkedMultiValueMap<>() {{
        add("offset", "0");
        add("limit", "20");
        add("sort", NameableSort.NAME_ASC.toString());
    }};

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
