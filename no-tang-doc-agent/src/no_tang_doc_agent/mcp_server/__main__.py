from no_tang_doc_agent.datetime_excepthook import apply_datetime_excepthook
from no_tang_doc_agent.mcp_server import mcp


def mcp_server() -> None:
    mcp.run(transport="streamable-http")


if __name__ == "__main__":
    apply_datetime_excepthook()
    mcp_server()
