from mcp.server.fastmcp import FastMCP

__all__ = [
    "mcp",
]

mcp = FastMCP(name="no-tang-doc-agent-mcp-server")


@mcp.tool()
async def echo(
    message: str,
) -> str:
    return message
