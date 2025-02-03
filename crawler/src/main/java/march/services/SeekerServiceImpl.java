package march.services;

import march.entities.Seeker;
import march.repositories.SeekerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeekerServiceImpl implements SeekerService {
    private final SeekerRepository seekerRepository;

    public SeekerServiceImpl(SeekerRepository seekerRepository) {
        this.seekerRepository = seekerRepository;
    }

    @Override
    public void createSeeker(Seeker seeker) {
        seekerRepository.save(seeker);
    }

    @Override
    public void updateSeeker(Seeker seeker) {
        seekerRepository.save(seeker);
    }

    @Override
    public void deleteSeeker(Integer id) {
        Optional<Seeker> seeker = seekerRepository.findById(id);
        if(seeker.isPresent()) {
            seekerRepository.deleteById(id);
        }
        else{
            throw new IllegalArgumentException("Seeker não encontrado");
        }
    }

    @Override
    public Seeker getBSeekerId(Integer id) {
        Optional<Seeker> seeker = seekerRepository.findById(id);
        return seeker.orElseThrow(() -> new IllegalArgumentException("Seeker não encontrado"));
    }

    @Override
    public Seeker getSeekerByApiName(String apiName) {
        Optional<Seeker> seeker = seekerRepository.findAllByNameApi(apiName);
        return seeker.orElseThrow(() -> new IllegalArgumentException("Seeker não encontrado"));
    }

    @Override
    public Seeker getSeekerByUrl(String url) {
        Optional<Seeker> seeker = seekerRepository.findAllByNameApi(url);
        return seeker.orElseThrow(() -> new IllegalArgumentException("Seeker não encontrado"));
    }

    @Override
    public List<Seeker> getSeekers() {
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());
        return seekerRepository.findAll(sort);
    }

    @Override
    public List<Seeker> getSeekersActiveProd() {
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());
        return seekerRepository.findAllByActiveProd(true,sort);
    }
}
