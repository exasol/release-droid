package com.exasol.releasedroid.usecases.request;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_CONFIG_PATH;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.repository.*;

public class ReleasePlatforms {
    private static final Logger LOGGER = Logger.getLogger(ReleasePlatforms.class.getName());
    private static final Set<PlatformName> DEPRECATED = Set.of(PlatformName.JIRA, PlatformName.COMMUNITY);

    public static ReleasePlatforms from(final UserInput userInput, final Repository repository) {
        final List<PlatformName> platforms = platforms(userInput, repository);
        if (platforms.isEmpty()) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-RD-20") //
                    .message("No release platform specified.") //
                    .mitigation("Please specify at least one release platform either on command line" //
                            + " or with key {{configuration key}} in file {{configuration file}}"
                            + " and re-run the Release Droid.", ReleaseConfigParser.RELEASE_PLATFORMS_KEY,
                            RELEASE_CONFIG_PATH)
                    .toString());
        }
        return new ReleasePlatforms(userInput.getGoal(), platforms, skipValidationOn(userInput),
                userInput.releaseGuide());
    }

    private static List<PlatformName> platforms(final UserInput userInput, final Repository repository) {
        if (userInput.hasPlatforms()) {
            return userInput.getPlatformNames();
        } else {
            return repository.getReleaseConfig() //
                    .map(ReleaseConfig::getReleasePlatforms) //
                    .orElse(Collections.emptyList());
        }
    }

    private static Set<PlatformName> skipValidationOn(final UserInput userInput) {
        if (userInput.skipValidation()) {
            return Arrays.stream(PlatformName.values()).collect(Collectors.toSet());
        } else {
            return Set.of();
        }
    }

    private final Goal goal;
    private final List<PlatformName> platforms;
    private final Set<PlatformName> skipValidationOn;
    private final Optional<Path> releaseGuide;

    /**
     * @param goal
     * @param platforms
     * @param skipValidationOn
     * @param b
     */
    public ReleasePlatforms(final Goal goal, final Collection<PlatformName> platforms,
            final Collection<PlatformName> skipValidationOn, final Optional<Path> releaseGuide) {
        this.goal = goal;
        this.platforms = removeDeprecated(platforms);
        this.skipValidationOn = new HashSet<>(skipValidationOn);
        this.releaseGuide = releaseGuide;
    }

    /**
     * @return list of platforms
     */
    public List<PlatformName> list() {
        return this.platforms;
    }

    public boolean isEmpty() {
        return this.platforms.isEmpty();
    }

    private List<PlatformName> removeDeprecated(final Collection<PlatformName> platforms) {
        final List<PlatformName> result = new ArrayList<>();
        for (final PlatformName p : platforms) {
            if (ReleasePlatforms.DEPRECATED.contains(p)) {
                LOGGER.warning(() -> ExaError.messageBuilder("W-RD-21") //
                        .message("Ignoring deprecated platform {{platform}}.", p)
                        .mitigation("Remove platform from file {{config file}} to avoid this warning.",
                                RELEASE_CONFIG_PATH)
                        .toString());
            } else {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * @param releasedPlatforms list of platforms that already have been released successfully in a former session
     * @return {@code true} if there are more platforms, waiting to be released
     */
    public boolean hasUnreleasedPlatforms(final Set<PlatformName> releasedPlatforms) {
        return !releasedPlatforms.containsAll(this.platforms);
    }

    /**
     * @param excluded list of platforms to exclude
     * @return new instance of {@link ReleasePlatforms} for the remaining platforms after removing the excluded
     *         platforms
     */
    public ReleasePlatforms remaining(final Collection<PlatformName> excluded, final Consumer<PlatformName> logger) {
        excluded.forEach(logger);
        final List<PlatformName> remaining = new ArrayList<>(this.platforms);
        remaining.removeAll(excluded);
        return new ReleasePlatforms(this.goal, remaining, this.skipValidationOn, this.releaseGuide);
    }

    /**
     * @return to return list of platforms to exclude from validation
     */
    public Set<PlatformName> skipValidationOn() {
        if (this.goal == Goal.RELEASE) {
            return this.skipValidationOn;
        }
        return Set.of(PlatformName.JIRA);
    }

    /**
     * @return optional path to release guide in case user requested to generate such
     */
    public Optional<Path> releaseGuide() {
        return this.releaseGuide;
    }

    public boolean createReleaseGuide() {
        return this.releaseGuide.isPresent();
    }
}
