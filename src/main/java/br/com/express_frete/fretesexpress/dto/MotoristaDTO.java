package br.com.express_frete.fretesexpress.dto;

import br.com.express_frete.fretesexpress.Validation.CpfCnpjValidator;
import br.com.express_frete.fretesexpress.Validation.ValidCpfCnpj;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class MotoristaDTO {
    private Long id;

    @NotBlank(message = "O nome completo eh obrigatorio")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nomeCompleto;

    @NotBlank(message = "O CPF ou CNPJ eh obrigatorio")
    @ValidCpfCnpj
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}|\\d{14}",
            message = "CPF/CNPJ invalido. Use o formato 000.000.000-00, 00000000000, 00.000.000/0000-00 ou 00000000000000")
    private String cpfCnpj;

    @NotBlank(message = "O e-mail eh obrigatorio")
    @Email(message = "E-mail invalido")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
    private String email;

    @NotBlank(message = "O numero da CNH eh obrigatorio")
    @Pattern(regexp = "\\d{11}", message = "CNH invalida. Deve conter 11 dígitos")
    private String cnh;

    @NotBlank(message = "O numero de telefone eh obrigatorio")
    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}|\\d{10,11}",
            message = "Numero invalido. Use o formato (00) 00000-0000 ou 00000000000")
    private String telefone;

    private List<VeiculoDTO> veiculos;
}