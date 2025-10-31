from mcp.server.auth.settings import AuthSettings
from pydantic import AnyHttpUrl

from no_tang_doc_agent.datetime_excepthook import apply_datetime_excepthook
from no_tang_doc_agent.mcp_server import (
    FastMCPSettings,
    JWTTokenVerifier,
    start_mcp_server,
)

if __name__ == "__main__":
    apply_datetime_excepthook()
    base_url = "http://localhost:8070"
    name = "no-tang-doc-agent-mcp-server"
    debug = True
    log_level = "INFO"
    host = "localhost"
    port = 8002
    issuer_url = "http://auth.local:8080/realms/ntdoc"
    required_scopes = ["mcp-user"]
    start_mcp_server(
        base_url=base_url,
        mcp_settings=FastMCPSettings(
            name=name,
            debug=debug,
            log_level=log_level,
            host=host,
            port=port,
            token_verifier=JWTTokenVerifier(),
            auth=AuthSettings(
                issuer_url=AnyHttpUrl(issuer_url),
                resource_server_url=AnyHttpUrl(f"http://{host}:{port}/mcp"),
                required_scopes=required_scopes,
            ),
        ),
    )
