package cn.authok.android.authentication.storage;

import cn.authok.android.util.Clock;

/**
 * Default Clock implementation used for verification.
 *
 * @see Clock
 * <p>
 * This class is thread-safe.
 */
final class ClockImpl implements Clock {

    ClockImpl() {
    }

    @Override
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
