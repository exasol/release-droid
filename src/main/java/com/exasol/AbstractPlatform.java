package com.exasol;

/**
 * An abstract base for classes implementing {@link Platform}.
 */
public abstract class AbstractPlatform implements Platform {
    private final PlatformName platformName;

    /**
     * An abstract base constructor.
     * 
     * @param platformName name of the platform
     */
    public AbstractPlatform(final PlatformName platformName) {
        this.platformName = platformName;
    }

    @Override
    public PlatformName getPlatformName() {
        return this.platformName;
    }
}