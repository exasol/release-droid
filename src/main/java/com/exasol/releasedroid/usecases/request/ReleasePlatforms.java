package com.exasol.releasedroid.usecases.request;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_CONFIG_PATH;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.repository.ReleaseConfig;
import com.exasol.releasedroid.usecases.repository.Repository;

public class ReleasePlatforms {
    private static final Logger LOGGER = Logger.getLogger(ReleasePlatforms.class.getName());
    private static final Set<PlatformName> DEPRECATED = Set.of(PlatformName.JIRA, PlatformName.COMMUNITY);

    public static ReleasePlatforms from(final UserInput userInput, final Repository repository) {
        final List<PlatformName> platforms = platforms(userInput, repository);
        if ((platforms == null) || platforms.isEmpty()) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-RD-20").message(
                    "Platform specified neither on commandline nor in configuration file {{configuration file}}.",
                    RELEASE_CONFIG_PATH) //
                    .mitigation("Please specify at least one platform and re-run the Release Droid.").toString());
        }
        return new ReleasePlatforms(userInput.getGoal(), platforms, skipValidationOn(userInput));
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

    /**
     * @param goal
     * @param platforms
     * @param skipValidationOn
     */
    public ReleasePlatforms(final Goal goal, final List<PlatformName> platforms,
            final Collection<PlatformName> skipValidationOn) {
        this.goal = goal;
        this.platforms = removeDeprecated(platforms);
        this.skipValidationOn = new HashSet<>(skipValidationOn);
    }

    /**
     * @return list of platforms
     */
    public List<PlatformName> list() {
        return this.platforms;
    }

    private List<PlatformName> removeDeprecated(final Collection<PlatformName> platforms) {
        final List<PlatformName> result = new ArrayList<>();
        for (final PlatformName p : platforms) {
            if (ReleasePlatforms.DEPRECATED.contains(p)) {
                LOGGER.warning(() -> ExaError.messageBuilder("E-RD-21") //
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
     * @param released list of platforms that have been released in an earlier session
     * @return {@link ReleasePlatforms} for which release is still pending
     */
    public ReleasePlatforms withoutReleased(final Collection<PlatformName> released) {
        final List<PlatformName> result = new ArrayList<>();
        for (final PlatformName p : this.platforms) {
            if (released.contains(p)) {
                LOGGER.info(() -> "Skipping " + p + " platform, as release has been already performed there.");
            } else {
                result.add(p);
            }
        }
        return new ReleasePlatforms(this.goal, result, this.skipValidationOn);
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
}
