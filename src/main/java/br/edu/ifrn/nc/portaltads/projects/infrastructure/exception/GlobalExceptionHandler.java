package br.edu.ifrn.nc.portaltads.projects.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());

        problem.setTitle("Recurso não encontrado");

        return problem;
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflictException(ConflictException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());

        problem.setTitle("Conflito de dados");

        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido ou com valor não permitido");

        problem.setTitle("Requisição inválida");

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Um ou mais campos são inválidos");

        problem.setTitle("Erro de validação");
        problem.setProperty("errors", extractErrors(ex));

        return problem;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "O parâmetro obrigatório '" + ex.getParameterName()
                        + "' não foi informado");

        problem.setTitle("Parâmetro obrigatório ausente");

        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Valor inválido para o parâmetro: " + ex.getName());

        problem.setTitle("Parâmetro inválido");

        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(
            IllegalArgumentException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());

        problem.setTitle("Argumento inválido");

        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        Throwable cause = ex;

        while (cause != null) {
            if (cause.getMessage() != null
                    && cause.getMessage().contains("chk_projeto_datas")) {

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "A data final não pode ser anterior à data inicial.");

                problem.setTitle("Datas inválidas");
                return problem;
            }

            cause = cause.getCause();
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Os dados informados violam uma restrição do banco de dados.");

        problem.setTitle("Conflito de dados");
        return problem;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "O arquivo excede o tamanho máximo permitido de 10 MB.");

        problem.setTitle("Arquivo muito grande");

        return problem;
    }

    @ExceptionHandler(CursoNaoEncontradoException.class)
    public ProblemDetail handleCursoNaoEncontrado(
            CursoNaoEncontradoException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage());

        problem.setTitle("Curso não encontrado");

        return problem;
    }

    @ExceptionHandler(ServicoCursoIndisponivelException.class)
    public ProblemDetail handleServicoCursoIndisponivel(
            ServicoCursoIndisponivelException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage());

        problem.setTitle("Serviço de cursos indisponível");

        return problem;
    }

    @ExceptionHandler(ErroIntegracaoException.class)
    public ProblemDetail handleErroIntegracao(ErroIntegracaoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY, ex.getMessage());
        problem.setTitle("Erro na integração com serviço externo");
        return problem;
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ProblemDetail handleTokenInvalido(TokenInvalidoException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, ex.getMessage());

        problem.setTitle("Não autenticado");

        return problem;
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ProblemDetail handleAcessoNegado(AcessoNegadoException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, ex.getMessage());

        problem.setTitle("Acesso negado");

        return problem;
    }

    @ExceptionHandler(ServicoUsuariosIndisponivelException.class)
    public ProblemDetail handleServicoUsuariosIndisponivel(
            ServicoUsuariosIndisponivelException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());

        problem.setTitle("Serviço de usuários indisponível");

        return problem;
    }

    @ExceptionHandler(CampusNaoEncontradoException.class)
    public ProblemDetail handleCampusNaoEncontrado(
            CampusNaoEncontradoException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());

        problem.setTitle("Campus não encontrado");

        return problem;
    }

    @ExceptionHandler(ServicoCampusIndisponivelException.class)
    public ProblemDetail handleServicoCampusIndisponivel(
            ServicoCampusIndisponivelException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage());

        problem.setTitle("Serviço de campus indisponível");

        return problem;
    }

    private Map<String, String> extractErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError :
                ex.getBindingResult().getFieldErrors()) {
            errors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage());
        }

        return errors;
    }

}

