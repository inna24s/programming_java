package lab.server;

import java.io.IOException;

/**
 * Исключение для {@link LimitedInputStream}ситуаций,
 * когда количество информации, прочитанное из потока,
 * выше установленного предел.
 */
class LimitException extends IOException {}
