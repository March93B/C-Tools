import logo from '../styles/logo.svg';
import '../styles/App.css'
import '../styles/index.css';
import seta from '../styles/seta-direita.png'
import seta2 from '../styles/a0358802956_65.jpg'

function CadastroMorador() {
  return (
    <div className="App">
      <div className="container">
        <main>
          <div className="py-5 text-center">
            <img className="d-block mx-auto mb-4" src={seta} style={{ transform: 'rotate(180deg)' }} alt="Bootstrap logo" width="72" height="57" />
            <h2>Cadastro de Moradores</h2>

            <p className="lead">
              Preencha o formulário para cadastrar um novo morador do condomínio
            </p>
          </div>

          <div className="row g-5" >
            <div className="col-md-5 col-lg-4 order-md-first justify-content-between align-items-center">
              <h5 className="align-items-center mb-3 ">
                <span className="text-primary ">Adicionar imagem do morador (Opcional)</span>
              </h5>
              <img 
            src={seta2}  
            alt="Descrição da Imagem"
            className="img-fluid mx-auto rounded-circle mb-3"
            width="293"
            height="293"
          />

              <form className="card p-2">
                  <button type="submit" className="btn btn-secondary">Escolher imagem</button>
              </form>
            </div>

            <div className="col-md-7 col-lg-8">
              <h4 className="mb-3">Informações</h4>
              <form className="needs-validation" noValidate>
                <div className="row g-3">
                  <div className="col-sm-6">
                    <label htmlFor="firstName" className="form-label">Nome</label>
                    <input type="text" className="form-control" id="firstName" placeholder="" required />
                    <div className="invalid-feedback">
                      Valid first name is required.
                    </div>
                  </div>

                  <div className="col-sm-6">
                    <label htmlFor="lastName" className="form-label">Sobrenome</label>
                    <input type="text" className="form-control" id="lastName" placeholder="" required />
                    <div className="invalid-feedback">
                      Valid last name is required.
                    </div>
                  </div>


                  <div className="col-12">
                    <label htmlFor="email" className="form-label">Email <span className="text-muted">(Opcional)</span></label>
                    <input type="email" className="form-control" id="email" placeholder="Exemplo: aaa@outlook.com" />
                    <div className="invalid-feedback">
                      Please enter a valid email address for shipping updates.
                    </div>
                  </div>


                  <div className="col-md-5">
                    <label htmlFor="country" className="form-label">Telefone</label>
                    <input type="number" className="form-control" id="telefone" placeholder="Exemplo: 1192345-6789" style={{
                      WebkitAppearance: 'none',
                      /* Usado para remover aparência de número no Firefox */
                      MozAppearance: 'textfield',
                      /* Para garantir que o espaçamento seja consistente */
                      margin: 0,
                    }} />

                    <div className="invalid-feedback">
                      Please select a valid country.
                    </div>
                  </div>

                  <div className="col-md-4">
                    <label htmlFor="state" className="form-label">Vagas</label>
                    <input type="number" className="form-control" id="vagas" placeholder="Exemplo: 3" />
                    <div className="invalid-feedback">
                      Please provide a valid state.
                    </div>
                  </div>

                  <div className="col-md-3">
                    <label htmlFor="zip" className="form-label">Apartamento</label>
                    <input type="number" className="form-control" id="apNumber" placeholder="Exemplo: 12" required />
                    <div className="invalid-feedback">
                      Zip code required.
                    </div>
                  </div>
                </div>

                <hr className="my-4" />


                <button className="w-100 btn btn-primary btn-lg mt-3" type="submit">Salvar Informações</button>
              </form>
            </div>
          </div>
        </main>

        <footer className="my-5 pt-5 text-muted text-center text-small">
          <p className="mb-1">&copy; 2024 ConDoni</p>
          <ul className="list-inline">
            <li className="list-inline-item"><a href="#">Privacy</a></li>
            <li className="list-inline-item"><a href="#">Terms</a></li>
            <li className="list-inline-item"><a href="#">Support</a></li>
          </ul>
        </footer>
      </div>
    </div>
  );
}

export default CadastroMorador;
