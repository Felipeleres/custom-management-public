/* ═══════════════════════════════════════════════════
   Sistema de Advocacia — script.js
   Arquitetura: SPA com seções estáticas no DOM.
   Zero HTML gerado em JS. Estado gerenciado em
   objetos centralizados. Modais limpam ao fechar.
═══════════════════════════════════════════════════ */

'use strict';

// ─────────────────────────────────────────────────
// AUTENTICAÇÃO — OAuth2 Password Grant
// ─────────────────────────────────────────────────
const AUTH = {
  tokenUrl:         '/oauth2/token',
  // Credenciais apenas para demo em Base64 (cliente_id:cliente_secret)
  basicCredentials: 'YXBlbmFzX3VtX3Rlc3RlOnNlbmhhX3BhcmFfZGVtbw==',
  loginPage:        'login.html',
  dashboardPage:    'index.html',
};

// Salva/lê/limpa tokens no sessionStorage
// (limpo automaticamente ao fechar o navegador)
const TokenStorage = {
  save(tokenResponse) {
    const expiresAt = Date.now() + (tokenResponse.expires_in - 60) * 1000;
    sessionStorage.setItem('access_token',  tokenResponse.access_token);
    sessionStorage.setItem('token_type',    tokenResponse.token_type ?? 'Bearer');
    sessionStorage.setItem('expires_at',    String(expiresAt));
    if (tokenResponse.refresh_token) {
      sessionStorage.setItem('refresh_token', tokenResponse.refresh_token);
    }
  },
  getAccessToken()  { return sessionStorage.getItem('access_token'); },
  getRefreshToken() { return sessionStorage.getItem('refresh_token'); },
  getTokenType()    { return sessionStorage.getItem('token_type') ?? 'Bearer'; },
  isExpired() {
    const exp = sessionStorage.getItem('expires_at');
    return !exp || Date.now() >= Number(exp);
  },
  isAuthenticated() { return !!this.getAccessToken() && !this.isExpired(); },
  clear() {
    ['access_token', 'token_type', 'expires_at', 'refresh_token']
      .forEach(k => sessionStorage.removeItem(k));
  },
};

// Guard — redireciona para login se não autenticado
function requireAuth() {
  if (!TokenStorage.isAuthenticated()) {
    window.location.href = AUTH.loginPage;
  }
}

// Logout — limpa tokens e volta ao login
function logout() {
  TokenStorage.clear();
  window.location.href = AUTH.loginPage;
}

// ─────────────────────────────────────────────────
// CONFIGURAÇÃO
// ─────────────────────────────────────────────────
const API = {
  cliente:    '/cliente',
  processo:   '/processo',
  pagamento:  '/pagamento',
  parcela:    '/parcela',
  financeiro: '/pagamento/total-pago',
};

const API_LIST = {
  cliente:   '/cliente?size=500&sort=id,desc',
  pagamento: '/pagamento?size=500&sort=id,desc',
  parcela:   '/parcela?size=999&sort=id,desc',
};

// ─────────────────────────────────────────────────
// ESTADO GLOBAL (caches em memória)
// ─────────────────────────────────────────────────
const state = {
  clientes:   [],
  processos:  [],
  pagamentos: [],
  parcelas:   [],
  pagamentoTemp: null, // dados do passo 1 ao criar pagamento
};

// ─────────────────────────────────────────────────
// UTILITÁRIOS
// ─────────────────────────────────────────────────

/** Navegação por Enter entre campos do modal */
function ativarNavegacaoEnter(modalId) {
  const modal = document.getElementById(modalId);
  const campos = Array.from(
    modal.querySelectorAll('input:not([type=hidden]), select, textarea')
  );

  campos.forEach((campo, index) => {
    campo.addEventListener('keydown', e => {
      if (e.key !== 'Enter') return;
      e.preventDefault();

      const proximo = campos[index + 1];
      if (proximo) {
        proximo.focus();
      }
    });
  });
}

/** Fetch autenticado — injeta Bearer token em todas as requisições */
async function apiFetch(url, options = {}) {
  const token = TokenStorage.getAccessToken();
  const res = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `${TokenStorage.getTokenType()} ${token}` } : {}),
    },
    ...options,
  });

  // Sessão expirada ou token inválido → volta ao login
  if (res.status === 401) {
    TokenStorage.clear();
    window.location.href = AUTH.loginPage;
    return;
  }

  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  // DELETE pode retornar 204 sem corpo
  if (res.status === 204) return null;
  return res.json();
}

function formatarCpf(cpf) {
  if (!cpf) return '';
  const s = String(cpf).replace(/\D/g, '');
  if (s.length !== 11) return s;
  return s.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

function formatarTelefone(t) {
  if (!t) return '';
  const s = String(t).replace(/\D/g, '');
  if (s.length === 11) return s.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
  if (s.length === 10) return s.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
  return s;
}

function formatarData(dataISO) {
  if (!dataISO) return '';
  const [ano, mes, dia] = dataISO.split('-');
  return `${dia}/${mes}/${ano}`;
}

function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  });
}

function badgeStatus(status) {
  const map = {
    PAGO:      ['badge-pago',   'Pago'],
    AGUARDANDO_PAGAMENTO: ['badge-aguardando', 'Aguardando Pagamento'],
    EM_ATRASO: ['badge-atraso', 'Em Atraso'],
  };
  const [cls, label] = map[status] ?? ['', status];
  return `<span class="badge ${cls}">${label}</span>`;
}

/** Exibe toast de feedback */
let _toastTimer;
function toast(msg, tipo = 'info') {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = `toast show ${tipo}`;
  clearTimeout(_toastTimer);
  _toastTimer = setTimeout(() => el.classList.remove('show'), 3500);
}

/** Máscaras inline */
function mascaraCpf(e) {
  let v = e.target.value.replace(/\D/g, '');
  v = v.replace(/(\d{3})(\d)/, '$1.$2');
  v = v.replace(/(\d{3})(\d)/, '$1.$2');
  v = v.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  e.target.value = v;
}

function mascaraTelefone(e) {
  let v = e.target.value.replace(/\D/g, '').substring(0, 11);
  if (v.length <= 10) {
    v = v.replace(/(\d{2})(\d)/, '($1) $2');
    v = v.replace(/(\d{4})(\d)/, '$1-$2');
  } else {
    v = v.replace(/(\d{2})(\d)/, '($1) $2');
    v = v.replace(/(\d{5})(\d)/, '$1-$2');
  }
  e.target.value = v;
}

// ─────────────────────────────────────────────────
// MODAIS — abrir / fechar
// ─────────────────────────────────────────────────
function abrirModal(id) {
  document.getElementById(id).classList.add('open');
}
function fecharModal(id) {
  document.getElementById(id).classList.remove('open');
}

/** Limpa todos os campos de um formulário dentro do modal */
function limparModal(id) {
  const modal = document.getElementById(id);
  modal.querySelectorAll('input:not([type=hidden]), select, textarea').forEach(el => {
    if (el.tagName === 'SELECT') el.selectedIndex = 0;
    else el.value = '';
  });
  modal.querySelectorAll('input[type=hidden]').forEach(el => el.value = '');
}

// ─────────────────────────────────────────────────
// NAVEGAÇÃO
// ─────────────────────────────────────────────────
function loadPage(page) {
  // Atualiza botões da nav
  document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.page === page);
  });

  // Ativa a seção correspondente
  document.querySelectorAll('.page').forEach(sec => {
    sec.classList.toggle('active', sec.id === `page-${page}`);
  });

  // Carrega dados da página ativada
  switch (page) {
    case 'dashboard':   carregarDashboard(); break;
    case 'clientes':    buscarClientes(); break;
    case 'processos':   buscarClientesEProcessos(); break;
    case 'financeiro':  carregarFinanceiro(); buscarPagamentos(); break;
  }
}

// ─────────────────────────────────────────────────
// DASHBOARD
// ─────────────────────────────────────────────────
async function carregarDashboard() {
  try {
    const [clientes, processos, financeiro] = await Promise.all([
      apiFetch(API.cliente),
      apiFetch(API.processo),
      apiFetch(API.financeiro),
    ]);

    const listaC = clientes.content ?? clientes ?? [];
    const listaP = processos.content ?? processos ?? [];

    document.getElementById('dash-clientes').textContent  = listaC.length;
    document.getElementById('dash-processos').textContent = listaP.length;
    document.getElementById('dash-pago').textContent      = formatarMoeda(financeiro.totalPago);
    document.getElementById('dash-aberto').textContent    = formatarMoeda(financeiro.totalEmAberto);
    document.getElementById('dash-atraso').textContent    = formatarMoeda(financeiro.totalEmAtraso);
  } catch (e) {
    console.error('Erro no dashboard', e);
  }
}

// ─────────────────────────────────────────────────
// CLIENTES
// ─────────────────────────────────────────────────
async function buscarClientes() {
  try {
    const dados = await apiFetch(API_LIST.cliente);
    state.clientes = dados.content ?? dados ?? [];
    renderizarClientes(state.clientes);
  } catch (e) {
    console.error('Erro ao buscar clientes', e);
    state.clientes = [];
    renderizarClientes([]);
  }
}

function renderizarClientes(lista) {
  const tbody = document.getElementById('tabelaClientes-body');
  if (!tbody) return;

  const empty = document.getElementById('clientes-empty');

  if (lista.length === 0) {
    tbody.innerHTML = '';
    empty.style.display = 'block';
    return;
  }

  empty.style.display = 'none';
  tbody.innerHTML = lista.map(c => `
    <tr>
      <td>${c.name ?? ''}</td>
      <td>${formatarCpf(c.cpf)}</td>
      <td>${c.email ?? ''}</td>
      <td>${formatarTelefone(c.telefone)}</td>
      <td class="col-actions">
        <button class="btn-icon" title="Editar" onclick="editarCliente(${c.id})">✏️</button>
        <button class="btn-icon del" title="Excluir" onclick="excluirCliente(${c.id})">🗑️</button>
      </td>
    </tr>
  `).join('');
}

function filtrarClientes() {
  const termo = document.getElementById('nomeClienteBusca').value.toLowerCase();
  const filtrados = state.clientes.filter(c =>
    (c.name?.toLowerCase().includes(termo)) ||
    (c.cpf?.includes(termo)) ||
    (c.email?.toLowerCase().includes(termo)) ||
    (c.telefone?.includes(termo))
  );
  renderizarClientes(filtrados);
}

function abrirModalCliente(titulo = 'Novo Cliente') {
  document.getElementById('modalCliente-title').textContent = titulo;
  abrirModal('modalCliente');
}

function fecharModalCliente() {
  limparModal('modalCliente');
  fecharModal('modalCliente');
}

async function editarCliente(id) {
  try {
    const c = await apiFetch(`${API.cliente}/${id}`);
    document.getElementById('clienteId').value       = c.id;
    document.getElementById('clienteNome').value     = c.name ?? '';
    document.getElementById('clienteCPF').value      = formatarCpf(c.cpf);
    document.getElementById('clienteEmail').value    = c.email ?? '';
    document.getElementById('clienteTelefone').value = formatarTelefone(c.telefone);
    abrirModalCliente('Editar Cliente');
  } catch (e) {
    toast('Erro ao carregar cliente.', 'error');
  }
}

async function salvarCliente() {
  const id = document.getElementById('clienteId').value;
  const payload = {
    name:     document.getElementById('clienteNome').value.trim(),
    cpf:      document.getElementById('clienteCPF').value.replace(/\D/g, ''),
    email:    document.getElementById('clienteEmail').value.trim(),
    telefone: document.getElementById('clienteTelefone').value.replace(/\D/g, ''),
  };

  if (!payload.name) {
      toast('Nome é obrigatório.', 'error');
      return;
    }

  try {
    await apiFetch(id ? `${API.cliente}/${id}` : API.cliente, {
      method: id ? 'PUT' : 'POST',
      body: JSON.stringify(payload),
    });
    fecharModalCliente();
    await buscarClientes();
    toast(id ? 'Cliente atualizado.' : 'Cliente cadastrado.', 'success');
  } catch (e) {
    toast('Erro ao salvar cliente.', 'error');
  }
}

async function excluirCliente(id) {
  if (!confirm('Excluir este cliente? Clientes com processos vinculados não podem ser removidos.')) return;
  try {
    await apiFetch(`${API.cliente}/${id}`, { method: 'DELETE' });
    await buscarClientes();
    toast('Cliente excluído.', 'success');
  } catch (e) {
    toast('Não foi possível excluir. Verifique se há processos vinculados.', 'error');
  }
}

// ─────────────────────────────────────────────────
// PROCESSOS
// ─────────────────────────────────────────────────

/** Carrega clientes E processos (para a página de processos) */
async function buscarClientesEProcessos() {
  await buscarClientes();
  preencherSelectClientes();
  await buscarProcessos();
}

function preencherSelectClientes(selectId = 'procClienteId') {
  const select = document.getElementById(selectId);
  if (!select) return;
  const valorAtual = select.value;
  select.innerHTML =
    '<option value="">Selecione um cliente…</option>' +
    state.clientes.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
  // restaura seleção anterior se ainda existir
  if (valorAtual) select.value = valorAtual;
}

async function buscarProcessos() {
  try {
    const dados = await apiFetch(API.processo);
    state.processos = dados.content ?? dados ?? [];
    renderizarProcessos(state.processos);
  } catch (e) {
    console.error('Erro ao buscar processos', e);
    state.processos = [];
    renderizarProcessos([]);
  }
}

function renderizarProcessos(lista) {
  const tbody = document.getElementById('tabelaProcessos-body');
  if (!tbody) return;

  const empty = document.getElementById('processos-empty');

  if (lista.length === 0) {
    tbody.innerHTML = '';
    empty.style.display = 'block';
    return;
  }

  empty.style.display = 'none';
  tbody.innerHTML = lista.map(p => `
    <tr>
      <td>${p.numero ?? ''}</td>
      <td>${p.descricao ?? ''}</td>
      <td>${formatarData(p.data)}</td>
      <td>${p.situacao ?? ''}</td>
      <td>${p.cliente?.name ?? '—'}</td>
      <td class="col-actions">
        <button class="btn-icon" title="Editar" onclick="editarProcesso(${p.id})">✏️</button>
        <button class="btn-icon del" title="Excluir" onclick="excluirProcesso(${p.id})">🗑️</button>
      </td>
    </tr>
  `).join('');
}

function filtrarProcessos() {
  const termo = document.getElementById('buscaProcessoInput').value.toLowerCase();
  const filtrados = state.processos.filter(p =>
    (p.numero?.toLowerCase().includes(termo)) ||
    (p.descricao?.toLowerCase().includes(termo)) ||
    (p.cliente?.name?.toLowerCase().includes(termo))
  );
  renderizarProcessos(filtrados);
}

function abrirModalProcesso(titulo = 'Novo Processo') {
  document.getElementById('modalProcesso-title').textContent = titulo;
  preencherSelectClientes('procClienteId');
  abrirModal('modalProcesso');
}

function fecharModalProcesso() {
  limparModal('modalProcesso');
  fecharModal('modalProcesso');
}

async function editarProcesso(id) {
  try {
    const p = await apiFetch(`${API.processo}/${id}`);

    // garante clientes carregados
    if (state.clientes.length === 0) await buscarClientes();
    preencherSelectClientes('procClienteId');

    document.getElementById('processoId').value       = p.id;
    document.getElementById('procNumero').value       = p.numero ?? '';
    document.getElementById('procDescricao').value    = p.descricao ?? '';
    document.getElementById('procData').value         = p.data ?? '';
    document.getElementById('procSituacao').value     = p.situacao ?? '';
    document.getElementById('procClienteId').value    = p.cliente?.id ?? '';

    abrirModalProcesso('Editar Processo');
  } catch (e) {
    toast('Erro ao carregar processo.', 'error');
  }
}

async function salvarProcesso() {
  const id = document.getElementById('processoId').value;
  const payload = {
    numero:     document.getElementById('procNumero').value.trim(),
    descricao:  document.getElementById('procDescricao').value.trim(),
    data:       document.getElementById('procData').value,
    situacao:   document.getElementById('procSituacao').value.trim(),
    clienteId:  document.getElementById('procClienteId').value,
  };

  if (!payload.numero || !payload.clienteId) {
    toast('Número e cliente são obrigatórios.', 'error');
    return;
  }

  try {
    await apiFetch(id ? `${API.processo}/${id}` : API.processo, {
      method: id ? 'PUT' : 'POST',
      body: JSON.stringify(payload),
    });
    fecharModalProcesso();
    await buscarProcessos();
    toast(id ? 'Processo atualizado.' : 'Processo cadastrado.', 'success');
  } catch (e) {
    toast('Erro ao salvar processo.', 'error');
  }
}

async function excluirProcesso(id) {
  if (!confirm('Excluir este processo?')) return;
  try {
    await apiFetch(`${API.processo}/${id}`, { method: 'DELETE' });
    await buscarProcessos();
    toast('Processo excluído.', 'success');
  } catch (e) {
    toast('Não foi possível excluir o processo.', 'error');
  }
}

// ─────────────────────────────────────────────────
// FINANCEIRO — totais
// ─────────────────────────────────────────────────
async function carregarFinanceiro() {
  try {
    const dados = await apiFetch(API.financeiro);
    document.getElementById('totalPago').textContent     = formatarMoeda(dados.totalPago);
    document.getElementById('totalEmAberto').textContent = formatarMoeda(dados.totalEmAberto);
    document.getElementById('totalEmAtraso').textContent = formatarMoeda(dados.totalEmAtraso);
    // atualiza também o dashboard se estiver visível
    const dp = document.getElementById('dash-pago');
    if (dp) {
      dp.textContent = formatarMoeda(dados.totalPago);
      document.getElementById('dash-aberto').textContent = formatarMoeda(dados.totalEmAberto);
      document.getElementById('dash-atraso').textContent = formatarMoeda(dados.totalEmAtraso);
    }
  } catch (e) {
    console.error('Erro ao carregar financeiro', e);
  }
}

// ─────────────────────────────────────────────────
// PAGAMENTOS
// ─────────────────────────────────────────────────
async function buscarPagamentos() {
  try {
    const dados = await apiFetch(API_LIST.pagamento);
    state.pagamentos = dados.content ?? dados ?? [];
    renderizarPagamentos(state.pagamentos);
  } catch (e) {
    console.error('Erro ao buscar pagamentos', e);
    state.pagamentos = [];
    renderizarPagamentos([]);
  }
}

function renderizarPagamentos(lista) {
  const tbody = document.getElementById('tabelaPagamentos-body');
  if (!tbody) return;

  const empty = document.getElementById('pagamentos-empty');

  if (lista.length === 0) {
    tbody.innerHTML = '';
    empty.style.display = 'block';
    return;
  }

  empty.style.display = 'none';
  tbody.innerHTML = lista.map(p => {
    const proc = state.processos.find(x => x.id === p.processoId);
    return `
      <tr>
        <td>${badgeStatus(p.statusPagamento)}</td>
        <td>${proc?.numero ?? '—'}</td>
        <td>${proc?.descricao ?? '—'}</td>
        <td>${proc?.data ? formatarData(proc.data) : '—'}</td>
        <td>${p.nomeCliente ?? '—'}</td>
        <td>${formatarMoeda(p.valorTotal)}</td>
        <td class="col-actions">
          <button class="btn-icon" title="Editar" onclick="editarPagamento(${p.id})">✏️</button>
          <button class="btn-icon del" title="Excluir" onclick="excluirPagamento(${p.id})">🗑️</button>
        </td>
      </tr>
    `;
  }).join('');
}

function filtrarPagamentos() {
  const termo = document.getElementById('buscaPagamentoInput').value.toLowerCase();
  const filtrados = state.pagamentos.filter(p => {
    const proc = state.processos.find(x => x.id === p.processoId);
    return (
      p.statusPagamento?.toLowerCase().includes(termo) ||
      proc?.numero?.toLowerCase().includes(termo) ||
      proc?.descricao?.toLowerCase().includes(termo) ||
      p.nomeCliente?.toLowerCase().includes(termo)
    );
  });
  renderizarPagamentos(filtrados);
}

async function abrirModalNovoPagamento() {
  limparModal('modalPagamento');
  if (state.processos.length === 0) await buscarProcessos();
  preencherSelectProcessos('pagProcesso');
  document.getElementById('modalPagamento-title').textContent = 'Novo Pagamento';
  abrirModal('modalPagamento');
}
/*
function preencherSelectProcessos(selectId) {
  const select = document.getElementById(selectId);
  if (!select) return;
  select.innerHTML =
    '<option value="">Selecione um processo…</option>' +
    state.processos.map(p => `<option value="${p.id}">${p.numero}</option>`).join('');
} */

/*
function preencherSelectProcessos(selectId) {
  const select = document.getElementById(selectId);
  if (!select) return;
  select.innerHTML =
    '<option value="">Selecione um processo…</option>' +
    state.processos.map(p => {
      const cliente = p.cliente?.name ?? '—';
      const numero  = p.numero ?? '—';
      return `<option value="${p.id}">${cliente} — ${numero}</option>`;
    }).join('');
}
*/
function preencherSelectProcessos() {
  const datalist = document.getElementById('pagProcessoList');
  if (!datalist) return;
  datalist.innerHTML = state.processos.map(p => {
    const cliente = p.cliente?.name ?? '—';
    const numero  = p.numero ?? '—';
    return `<option value="${cliente} — ${numero}" data-id="${p.id}">`;
  }).join('');

  // Ao selecionar uma opção, salva o id no campo hidden
  const input = document.getElementById('pagProcessoInput');
  input.addEventListener('input', () => {
    const match = state.processos.find(p => {
      const label = `${p.cliente?.name ?? '—'} — ${p.numero ?? '—'}`;
      return label === input.value;
    });
    document.getElementById('pagProcesso').value = match ? match.id : '';
  });
}


function fecharModalPagamento() {
  limparModal('modalPagamento');
  document.getElementById('pagProcessoInput').value = '';
  fecharModal('modalPagamento');
}

async function editarPagamento(id) {
  try {
    const p = await apiFetch(`${API.pagamento}/${id}`);
    if (state.processos.length === 0) await buscarProcessos();
    preencherSelectProcessos('pagProcesso');

    document.getElementById('pagamentoId').value      = p.id;
    document.getElementById('pagProcesso').value      = p.processo?.id ?? '';
    document.getElementById('pagValorTotal').value    = p.valorTotal ?? '';
    document.getElementById('pagQtdParcelas').value   = p.quantidadeParcelas ?? '';
    document.getElementById('pagStatus').value        = p.statusPagamento ?? 'AGUARDANDO_PAGAMENTO';

    // Preenche o campo de texto com o valor legível
    const proc = state.processos.find(x => x.id === p.processo?.id);
    if (proc) {
      document.getElementById('pagProcessoInput').value =
        `${proc.cliente?.name ?? '—'} — ${proc.numero ?? '—'}`;
    }

    document.getElementById('modalPagamento-title').textContent = 'Editar Pagamento';
    abrirModal('modalPagamento');
  } catch (e) {
    toast('Erro ao carregar pagamento.', 'error');
  }
}

/** Avança do passo 1 (dados do pagamento) para o passo 2 (parcelas) */
function avancarParaParcelas() {
  const valorTotal = Number(document.getElementById('pagValorTotal').value);
  const qtd        = Number(document.getElementById('pagQtdParcelas').value);
  const processoId = document.getElementById('pagProcesso').value;
  const status     = document.getElementById('pagStatus').value;

  if (!processoId) { toast('Selecione um processo.', 'error'); return; }
  if (!qtd || qtd < 1) { toast('Informe uma quantidade válida de parcelas.', 'error'); return; }
  if (!valorTotal || valorTotal <= 0) { toast('Informe o valor total.', 'error'); return; }

  state.pagamentoTemp = {
    processoID:          processoId,
    statusPagamento:     status,
    quantidadeParcelas:  qtd,
    valorTotal:          valorTotal,
    parcelas:            [],
  };

  const valorParcela = (valorTotal / qtd).toFixed(2);

  // Monta as linhas de parcela no modal
  const container = document.getElementById('parcelasContainer');
  container.innerHTML = Array.from({ length: qtd }, (_, i) => `
    <div class="parcela-row">
      <span class="parcela-index">Parcela ${i + 1}</span>
      <div>
        <label>Valor (R$)</label>
        <input type="number" class="parcela-valor" min="0" step="0.01" value="${valorParcela}" />
      </div>
      <div>
        <label>Data de vencimento</label>
        <input type="date" class="parcela-data" />
      </div>
    </div>
  `).join('');

  fecharModal('modalPagamento');
  abrirModal('modalParcelas');
}

function voltarParaPagamento() {
  fecharModal('modalParcelas');
  abrirModal('modalPagamento');
}

async function salvarPagamento() {
  if (!state.pagamentoTemp) return;

  const valores = document.querySelectorAll('.parcela-valor');
  const datas   = document.querySelectorAll('.parcela-data');

  state.pagamentoTemp.parcelas = Array.from(valores).map((v, i) => ({
    valor:           Number(v.value),
    dataParcela:     datas[i].value,
    statusPagamento: state.pagamentoTemp.statusPagamento,
  }));

  // Se estiver editando (pagamentoId preenchido), usa PUT; senão POST
  const id = document.getElementById('pagamentoId').value;

  try {
    await apiFetch(id ? `${API.pagamento}/${id}` : API.pagamento, {
      method: id ? 'PUT' : 'POST',
      body: JSON.stringify(state.pagamentoTemp),
    });

    fecharModal('modalParcelas');
    limparModal('modalPagamento');
    document.getElementById('pagamentoId').value = '';
    state.pagamentoTemp = null;

    await Promise.all([buscarPagamentos(), carregarFinanceiro()]);
    toast(id ? 'Pagamento atualizado.' : 'Pagamento cadastrado.', 'success');
  } catch (e) {
    toast('Erro ao salvar pagamento.', 'error');
  }
}

async function excluirPagamento(id) {
  if (!confirm('Excluir este pagamento?')) return;
  try {
    await apiFetch(`${API.pagamento}/${id}`, { method: 'DELETE' });
    await Promise.all([buscarPagamentos(), carregarFinanceiro()]);
    toast('Pagamento excluído.', 'success');
  } catch (e) {
    toast('Não foi possível excluir o pagamento.', 'error');
  }
}

// ─────────────────────────────────────────────────
// PARCELAS
// ─────────────────────────────────────────────────
async function buscarParcelas() {
  try {
    const dados = await apiFetch(API_LIST.parcela);
    state.parcelas = dados.content ?? dados ?? [];
    renderizarParcelas(state.parcelas);
  } catch (e) {
    console.error('Erro ao buscar parcelas', e);
    state.parcelas = [];
    renderizarParcelas([]);
  }
}

function renderizarParcelas(lista) {
  const tbody = document.getElementById('tabelaParcelas-body');
  if (!tbody) return;

  const empty = document.getElementById('parcelas-empty');

  if (lista.length === 0) {
    tbody.innerHTML = '';
    empty.style.display = 'block';
    return;
  }

  empty.style.display = 'none';
  tbody.innerHTML = lista.map(p => `
    <tr>
      <td>${badgeStatus(p.statusPagamento)}</td>
      <td>${p.numeroProcesso ?? '—'}</td>
      <td>${p.nomeCliente ?? '—'}</td>
      <td>${formatarData(p.dataParcela)}</td>
      <td>${formatarMoeda(p.valor)}</td>
      <td class="col-actions">
        <button class="btn-icon" title="Editar" onclick="editarParcela(${p.id})">✏️</button>
      </td>
    </tr>
  `).join('');
}

function filtrarParcelas() {
  const termo = document.getElementById('buscaParcelaInput').value.toLowerCase();
  const filtradas = state.parcelas.filter(p =>
    p.statusPagamento?.toLowerCase().includes(termo) ||
    p.numeroProcesso?.toLowerCase().includes(termo) ||
    p.nomeCliente?.toLowerCase().includes(termo)
  );
  renderizarParcelas(filtradas);
}

async function editarParcela(id) {
  try {
    const p = await apiFetch(`${API.parcela}/${id}`);
    document.getElementById('parcelaId').value     = p.id;
    document.getElementById('parcelaValor').value  = p.valor ?? '';
    document.getElementById('parcelaData').value   = p.dataParcela ?? '';
    document.getElementById('parcelaStatus').value = p.statusPagamento ?? 'AGUARDANDO_PAGAMENTO';
    abrirModal('modalParcela');
  } catch (e) {
    toast('Erro ao carregar parcela.', 'error');
  }
}

async function salvarParcela() {
  const id = document.getElementById('parcelaId').value;
  const payload = {
    valor:           Number(document.getElementById('parcelaValor').value),
    dataParcela:     document.getElementById('parcelaData').value,
    statusPagamento: document.getElementById('parcelaStatus').value,
  };

  try {
    await apiFetch(`${API.parcela}/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
    limparModal('modalParcela');
    fecharModal('modalParcela');
    await Promise.all([buscarParcelas(), carregarFinanceiro()]);
    toast('Parcela atualizada.', 'success');
  } catch (e) {
    toast('Erro ao salvar parcela.', 'error');
  }
}

// ─────────────────────────────────────────────────
// ABAS (financeiro)
// ─────────────────────────────────────────────────
function ativarAba(aba) {
  document.querySelectorAll('.tab-btn').forEach(btn =>
    btn.classList.toggle('active', btn.id === `tab-${aba}`)
  );
  document.querySelectorAll('.tab-panel').forEach(panel =>
    panel.classList.toggle('active', panel.id === `painel-${aba}`)
  );

  if (aba === 'pagamentos') {
    buscarPagamentos();
    carregarFinanceiro();
  } else {
    buscarParcelas();
  }
}

// ─────────────────────────────────────────────────
// EVENTOS
// ─────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {

  // Navegação sidebar
  document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.addEventListener('click', () => loadPage(btn.dataset.page));
  });

  // ── Clientes ──
  document.getElementById('btn-novo-cliente').addEventListener('click', () => {
    limparModal('modalCliente');
    abrirModalCliente('Novo Cliente');
  });
  document.getElementById('btn-salvar-cliente').addEventListener('click', salvarCliente);
  document.getElementById('btn-cancelar-cliente').addEventListener('click', fecharModalCliente);
  document.getElementById('nomeClienteBusca').addEventListener('input', filtrarClientes);

  // máscaras
  document.getElementById('clienteCPF').addEventListener('input', mascaraCpf);
  document.getElementById('clienteTelefone').addEventListener('input', mascaraTelefone);

  // ── Processos ──
  document.getElementById('btn-novo-processo').addEventListener('click', () => {
    limparModal('modalProcesso');
    abrirModalProcesso('Novo Processo');
  });
  document.getElementById('btn-salvar-processo').addEventListener('click', salvarProcesso);
  document.getElementById('btn-cancelar-processo').addEventListener('click', fecharModalProcesso);
  document.getElementById('buscaProcessoInput').addEventListener('input', filtrarProcessos);

  // ── Pagamentos ──
  document.getElementById('btn-novo-pagamento').addEventListener('click', abrirModalNovoPagamento);
  document.getElementById('btn-avancar-pagamento').addEventListener('click', avancarParaParcelas);
  document.getElementById('btn-cancelar-pagamento').addEventListener('click', fecharModalPagamento);
  document.getElementById('btn-salvar-pagamento').addEventListener('click', salvarPagamento);
  document.getElementById('btn-voltar-pagamento').addEventListener('click', voltarParaPagamento);
  document.getElementById('buscaPagamentoInput').addEventListener('input', filtrarPagamentos);

  // ── Parcelas ──
  document.getElementById('btn-salvar-parcela').addEventListener('click', salvarParcela);
  document.getElementById('btn-cancelar-parcela').addEventListener('click', () => {
    limparModal('modalParcela');
    fecharModal('modalParcela');
  });
  document.getElementById('buscaParcelaInput').addEventListener('input', filtrarParcelas);

  // ── Abas financeiro ──
  document.getElementById('tab-pagamentos').addEventListener('click', () => ativarAba('pagamentos'));
  document.getElementById('tab-parcelas').addEventListener('click', () => ativarAba('parcelas'));

  // Inicia no dashboard
  loadPage('dashboard');

 // Navegação por Enter nos modais
 ativarNavegacaoEnter('modalCliente');
 ativarNavegacaoEnter('modalProcesso');
 ativarNavegacaoEnter('modalPagamento');
 ativarNavegacaoEnter('modalParcela');

});

// ─────────────────────────────────────────────────
// LOGIN FORM (executado apenas na login.html)
// ─────────────────────────────────────────────────
(function initLogin() {
  const form = document.getElementById('loginForm');
  if (!form) {
    // Estamos no index.html — verifica autenticação
    requireAuth();
    return;
  }

  // Se já estiver autenticada, vai direto ao dashboard
  if (TokenStorage.isAuthenticated()) {
    window.location.href = AUTH.dashboardPage;
    return;
  }

  const btnLogin      = document.getElementById('btnLogin');
  const loginAlert    = document.getElementById('loginAlert');
  const fieldUser     = document.getElementById('fieldUsername');
  const fieldPass     = document.getElementById('fieldPassword');
  const errUser       = document.getElementById('errorUsername');
  const errPass       = document.getElementById('errorPassword');
  const inputUser     = document.getElementById('loginUsername');
  const inputPass     = document.getElementById('loginPassword');
  const togglePw      = document.getElementById('togglePw');
  const iconEye       = document.getElementById('iconEye');
  const iconEyeOff    = document.getElementById('iconEyeOff');

  // Toggle visibilidade da senha
  togglePw.addEventListener('click', () => {
    const isText = inputPass.type === 'text';
    inputPass.type         = isText ? 'password' : 'text';
    iconEye.style.display    = isText ? 'block' : 'none';
    iconEyeOff.style.display = isText ? 'none'  : 'block';
  });

  // Limpa erros ao digitar
  inputUser.addEventListener('input', () => fieldUser.classList.remove('has-error'));
  inputPass.addEventListener('input', () => fieldPass.classList.remove('has-error'));

  function showAlert(msg) {
    loginAlert.textContent = msg;
    loginAlert.classList.add('visible');
  }

  function hideAlert() {
    loginAlert.classList.remove('visible');
  }

  function setLoading(on) {
    btnLogin.disabled = on;
    btnLogin.classList.toggle('loading', on);
  }

  function validate() {
    let ok = true;
    if (!inputUser.value.trim()) {
      fieldUser.classList.add('has-error');
      errUser.textContent = 'Informe o e-mail';
      ok = false;
    }
    if (!inputPass.value) {
      fieldPass.classList.add('has-error');
      errPass.textContent = 'Informe a senha';
      ok = false;
    }
    return ok;
  }

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert();

    if (!validate()) return;

    setLoading(true);
    try {
      const body = new URLSearchParams({
        grant_type: 'password',
        username:   inputUser.value.trim(),
        password:   inputPass.value,
      });

      const res = await fetch(AUTH.tokenUrl, {
        method:  'POST',
        headers: {
          'Content-Type':  'application/x-www-form-urlencoded',
          'Authorization': `Basic ${AUTH.basicCredentials}`,
        },
        body,
      });

      if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.error_description ?? `Erro ${res.status}`);
      }

      const tokenResponse = await res.json();
      TokenStorage.save(tokenResponse);
      window.location.href = AUTH.dashboardPage;

    } catch (err) {
      const msg = err.message?.toLowerCase() ?? '';
      showAlert(
        msg.includes('bad credentials') || msg.includes('invalid')
          ? 'E-mail ou senha incorretos.'
          : (err.message || 'Não foi possível conectar ao servidor.')
      );
    } finally {
      setLoading(false);
    }
  });
})();
