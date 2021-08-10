package com.rorschach.springwebflux.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author rorschach
 * @date 2021/8/10 9:29
 */
@RestController
@RequestMapping("/hello")
@Slf4j
public class HelloController {

    @GetMapping("mono")
    public Mono<Object> mono() {
//        return Mono.just("hello webflux");
        return Mono.create(stringMonoSink -> {
            log.info("创建 mono");
            stringMonoSink.success("hello, webflux");
        })
        // 当订阅者去订阅发布者的时候，该方法会调用
        .doOnSubscribe(subscription -> log.info("{}", subscription))
        // 当订阅者收到数据时，改方法会调用
        .doOnNext(o -> log.info("{}", o));
    }

//    @GetMapping("flux")
//    public Flux<String> flux() {
//        return Flux.just("hello","webflux","spring","boot");
//    }

    /**
     * 阻塞5秒钟
     * @return String
     */
    private String createStr() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "some string";
    }

    /** 普通的SpringMVC方法 */
    @GetMapping("/1")
    private String get1() {
        log.info("get1 start");
        String result = createStr();
        log.info("get1 end.");
        return result;
    }

    /** WebFlux(返回的是Mono) */
    @GetMapping("/2")
    private Mono<String> get2() {
        log.info("get2 start");
        Mono<String> result = Mono.fromSupplier(this::createStr);
        log.info("get2 end.");
        return result;
    }

    @GetMapping(value = "/3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<String> flux() {
        return Flux
                .fromStream(IntStream.range(1, 999).mapToObj(i -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "fuck you--" + i;
                }));
    }
}
