package com.uca.pncparcialfinalrestaurante.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uca.pncparcialfinalrestaurante.dto.request.UsuarioRequest;
import com.uca.pncparcialfinalrestaurante.entities.Sucursal;
import com.uca.pncparcialfinalrestaurante.entities.Usuario;
import com.uca.pncparcialfinalrestaurante.exception.BusinessRuleException;
import com.uca.pncparcialfinalrestaurante.exception.EmailAlreadyExistsException;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.repository.SucursalRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.RoleName;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con el correo " + request.getEmail());
        }

        Sucursal sucursal = resolverSucursal(request.getRol(), request.getSucursalId());

        Usuario usuario = Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .sucursal(sucursal)
                .build();

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    private Sucursal resolverSucursal(RoleName rol, Long sucursalId) {
        if (rol != RoleName.ENCARGADO_TURNO) {
            return null;
        }

        if (sucursalId == null) {
            throw new BusinessRuleException("Un Encargado de turno debe tener una sucursal asignada");
        }

        return sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id " + sucursalId));
    }
}
