package org.jskele.libs.embeddedpostgres;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.store.PostgresArtifactStoreBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.embed.postgresql.Command;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.PackagePaths;
import ru.yandex.qatools.embed.postgresql.config.PostgresDownloadConfigBuilder;
import ru.yandex.qatools.embed.postgresql.config.RuntimeConfigBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Collections.emptyList;

@Slf4j
@Getter
class EmbeddedPostgresBean {
    private final EmbeddedPostgres postgres = new EmbeddedPostgres();

    private String url;

    @PostConstruct
    void init() throws IOException {

        Path path = Paths.get(System.getProperty("user.home"), ".embedpostgresql", "embedded");
        url = postgres.start(
                cachedRuntimeConfig(path),
                "localhost",
                5433,
                "embedded",
                "embedded",
                "embedded",
                emptyList());

        log.info("starting postgres: {}", url);
    }

    private static IRuntimeConfig cachedRuntimeConfig(Path cachedPath) {
        final Command cmd = Command.Postgres;
        final FixedPath cachedDir = new FixedPath(cachedPath.toString());
        return new RuntimeConfigBuilder()
                .defaults(cmd)
                .daemonProcess(false)
                .artifactStore(new PostgresArtifactStoreBuilder()
                        .defaults(cmd)
                        .tempDir(cachedDir)
                        .download(new PostgresDownloadConfigBuilder()
                                .defaultsForCommand(cmd)
                                .packageResolver(new PackagePaths(cmd, cachedDir))
                                .build()))
                .build();
    }

    @PreDestroy
    void cleanup() {
        postgres.stop();
    }
}