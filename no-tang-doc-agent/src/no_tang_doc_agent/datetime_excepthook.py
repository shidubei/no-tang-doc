import datetime as dt
import sys
import traceback as tb
from types import TracebackType

__all__ = [
    "apply_datetime_excepthook",
]


def _datetime_excepthook(
    exc_type: type[BaseException],
    exc_value: BaseException,
    exc_traceback: TracebackType | None,
) -> None:
    print(dt.datetime.now(), file=sys.stderr)
    tb.print_exception(exc_type, exc_value, exc_traceback)


def apply_datetime_excepthook() -> None:
    sys.excepthook = _datetime_excepthook
