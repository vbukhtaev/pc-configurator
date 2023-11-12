package ru.bukhtaev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.util.*;

/**
 * Абстрактный интеграционный тест.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public abstract class AbstractIntegrationTest extends AbstractContainerizedTest {

    /**
     * Параметр для запроса на получение сущностей, имеющих название, с пагинацией.
     */
    protected static final LinkedMultiValueMap<String, String> NAMEABLE_PAGE_REQUEST_PARAMS
            = new LinkedMultiValueMap<>() {{
        add("offset", "0");
        add("limit", "20");
        add("sort", NameableSort.NAME_ASC.toString());
    }};

    /**
     * Параметр для запроса на получение вариантов исполнения с пагинацией.
     */
    protected static final LinkedMultiValueMap<String, String> DESIGN_PAGE_REQUEST_PARAMS
            = new LinkedMultiValueMap<>() {{
        add("offset", "0");
        add("limit", "20");
        add("sort", DesignSort.VENDOR_NAME_ASC.toString());
    }};

    /**
     * Параметр для запроса на получение чипсетов с пагинацией.
     */
    protected static final LinkedMultiValueMap<String, String> CHIPSET_PAGE_REQUEST_PARAMS
            = new LinkedMultiValueMap<>() {{
        add("offset", "0");
        add("limit", "20");
        add("sort", ChipsetSort.SOCKET_NAME_ASC.toString());
    }};

    /**
     * Параметр для запроса на получение вентиляторов с пагинацией.
     */
    protected static final LinkedMultiValueMap<String, String> FAN_PAGE_REQUEST_PARAMS
            = new LinkedMultiValueMap<>() {{
        add("offset", "0");
        add("limit", "20");
        add("sort", FanSort.NAME_ASC.toString());
    }};

    /**
     * Параметр для запроса на получение графических процессоров с пагинацией.
     */
    protected static final LinkedMultiValueMap<String, String> GPU_PAGE_REQUEST_PARAMS
            = new LinkedMultiValueMap<>() {{
        add("offset", "0");
        add("limit", "20");
        add("sort", GpuSort.MANUFACTURER_NAME_ASC.toString());
    }};

    /**
     * Параметр для запроса на получение жестких дисков с пагинацией.
     */
    protected static final LinkedMultiValueMap<String, String> HDD_PAGE_REQUEST_PARAMS
            = new LinkedMultiValueMap<>() {{
        add("offset", "0");
        add("limit", "20");
        add("sort", HddSort.VENDOR_NAME_ASC.toString());
    }};

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
